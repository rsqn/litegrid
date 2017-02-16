package com.mac.litegrid.clustering.communication.workers;

import com.google.common.collect.Lists;
import com.mac.libraries.concurrency.KeepRunning;
import com.mac.libraries.concurrency.Notifier;
import com.mac.libraries.configuration.ConfigurationSource;
import com.mac.libraries.configuration.MapConfigurationSource;
import com.mac.libraries.generic.RandomUtil;
import com.mac.libraries.generic.UIDUtil;
import com.mac.libraries.network.NetworkUtilities;
import com.mac.libraries.statistics.StatisticsRecorder;
import com.mac.litegrid.clustering.Constants;
import com.mac.litegrid.clustering.communication.channels.TCPChannel;
import com.mac.litegrid.clustering.communication.channels.UDPBroadCastingChannel;
import com.mac.litegrid.clustering.communication.channels.UDPMulticastChannel;
import com.mac.litegrid.clustering.communication.events.MemberJoinEvent;
import com.mac.litegrid.clustering.communication.events.MemberLeaveEvent;
import com.mac.litegrid.clustering.communication.interfaces.MGroupListener;
import com.mac.litegrid.clustering.communication.interfaces.MGroupMessage;
import com.mac.litegrid.clustering.communication.interfaces.MulticastChannel;
import com.mac.litegrid.clustering.communication.interfaces.NetworkMessage;
import com.mac.litegrid.clustering.communication.messages.*;
import com.mac.litegrid.clustering.communication.packets.PacketInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: mandrewes
 * Date: 11/01/2005
 * <p>
 * To change this template use File | Settings | File Templates.
 */
public class LiteGrid extends Thread {
    protected Logger log = LoggerFactory.getLogger(getClass());
    private KeepRunning keepRunning;
    public static final String COMM_CHANNEL_SUFFIX = "-COMM";
    private Hashtable<String, Member> otherMembers;
    private String gridName;

    private MulticastChannel discoveryChannel; // work around for tcp only memberChannels not supporting true multicast
    private MulticastChannel memberChannel;
    private MulticastChannel commChannel; // needs two channels - if something big is coming in our out (in testing 100mb file), otherMembers could (did) drop out
    private BlockingQueue receiveQueue;
    private Member self;
    private Peer localPeer;
    private Notifier notifier;
    private FragmentManager fragmentManager; // for UDP
    private static final int HEARTBEAT_DELAY = 3000;
    private static final int EXPIRE_AFTER = HEARTBEAT_DELAY * 3;
    private static final boolean REDUCE_MEMBER_POLLING = true;
    private long lastHeartbeat;
    private Object joinNotify = new Object();
    private boolean assumeJoined = false;

    private int MIN_MEMBERS_FOR_MASTER_SELECTION = 2;
    private int maxMemberCount = 0; // used to allow master selection if a grid of two loses on member
    private boolean maySelectMasterOnPartition = false;
    private Member lastMaster;


    //todo: view comparison
    private ConfigurationSource configSource;



    protected LiteGrid(String address, String gridName, ConfigurationSource config) throws Exception {
        this.setDaemon(true);
        this.keepRunning = new KeepRunning();
        this.gridName = gridName;
        this.configSource = config;

        if (this.configSource == null) {
            this.configSource = new MapConfigurationSource();
        }

        this.otherMembers = new Hashtable();
        this.receiveQueue = new ArrayBlockingQueue(10000);
        this.notifier = new Notifier();

        this.localPeer = new Peer();
        this.self = generateSelf();

        this.fragmentManager = new FragmentManager(self);
        this.fragmentManager.setQueue(receiveQueue);

        int minMembers = config.getIntValue("min.members.for.master.selection", 2);
        if (minMembers > 0) {
            MIN_MEMBERS_FOR_MASTER_SELECTION = minMembers;
        }

        this.maySelectMasterOnPartition = config.getBoolValue("may.select.master.on.partition");

        log.info("minimum members for master selection " + MIN_MEMBERS_FOR_MASTER_SELECTION);
        final int ONE_HUNDRED_FOR_SOME_REASON_I_CANT_REMEMBER = 100;
        int tcpMemberChannelPort = LiteGridManager.getUniquePort(gridName);
        int tcpCommsChannelPort = LiteGridManager.getUniquePort(gridName + COMM_CHANNEL_SUFFIX);

        if (configSource.getBoolValue("use.tcp.only")) {
            if (configSource.hasValue("tcp.membership.bind.port")) {
                tcpMemberChannelPort = configSource.getIntValue("tcp.membership.bind.port");
                log.info("tcp.membership.bind.port overridden by configuration to " + tcpMemberChannelPort);
            }

            if (configSource.hasValue("tcp.comms.bind.port")) {
                tcpCommsChannelPort = configSource.getIntValue("tcp.comms.bind.port");
                log.info("tcp.comms.bind.port overriden by configuration to " + tcpCommsChannelPort);
            }


            int maxMemberChannelPort = tcpMemberChannelPort + ONE_HUNDRED_FOR_SOME_REASON_I_CANT_REMEMBER;
            log.info("MemberChannel looking for an free port between " + tcpMemberChannelPort + " and " + maxMemberChannelPort);
            while (tcpMemberChannelPort < maxMemberChannelPort) {
                if (TCPChannel.isPortAvailable(tcpMemberChannelPort)) {
                    log.info("MemberChannel port " + tcpMemberChannelPort + " is available - using");
                    localPeer.setTcpMemberChannelPort(tcpMemberChannelPort);
                    memberChannel = new TCPChannel(TCPChannel.ChannelType.MEMBERSHIP, self, tcpMemberChannelPort, configSource, receiveQueue);
                    break;
                }
                log.info("MemberChannel port " + tcpMemberChannelPort + " is NOT available - incrementing");
                tcpMemberChannelPort++;
            }

            int maxCommChannelPort = tcpCommsChannelPort + ONE_HUNDRED_FOR_SOME_REASON_I_CANT_REMEMBER;
            log.info("CommChannel looking for an free port between " + tcpCommsChannelPort + " and " + maxCommChannelPort);

            while (tcpCommsChannelPort < maxCommChannelPort) {
                if (TCPChannel.isPortAvailable(tcpCommsChannelPort)) {
                    log.info("CommChannel port " + tcpCommsChannelPort + " is available - using");
                    localPeer.setTcpCommsChannelPort(tcpCommsChannelPort);
                    commChannel = new TCPChannel(TCPChannel.ChannelType.COMMUNICATIONS, self, tcpCommsChannelPort, configSource, receiveQueue);
                    break;
                }
                log.info("CommChannel port " + tcpCommsChannelPort + " is NOT available - incrementing");

                tcpCommsChannelPort++;

            }
        } else {
            memberChannel = new UDPMulticastChannel(UDPMulticastChannel.ChannelType.MEMBERSHIP, self, address, LiteGridManager.getUniquePort(gridName), fragmentManager);
            commChannel = new UDPMulticastChannel(UDPMulticastChannel.ChannelType.COMMUNICATIONS, self, address, LiteGridManager.getUniquePort(gridName + COMM_CHANNEL_SUFFIX), fragmentManager);
        }


        memberChannel.startup();
        commChannel.startup();

        if (configSource.getBoolValue("use.udp_spamming.discovery")) {
            int discoveryMemberChannelPort = LiteGridManager.getUniquePort(gridName);


            //todo - do this in the channel not in here
            int maxMemberChannelPort = discoveryMemberChannelPort + ONE_HUNDRED_FOR_SOME_REASON_I_CANT_REMEMBER;
            log.info("MemberChannel looking for an free port between " + discoveryMemberChannelPort + " and " + maxMemberChannelPort);
            while (discoveryMemberChannelPort < maxMemberChannelPort) {
                if (UDPBroadCastingChannel.isPortAvailable(discoveryMemberChannelPort)) {
                    log.info("Discovery MemberChannel port " + discoveryMemberChannelPort + " is available - using");
                    discoveryChannel = new UDPBroadCastingChannel(UDPBroadCastingChannel.ChannelType.MEMBERSHIP, self, address, discoveryMemberChannelPort, fragmentManager, config);
                    localPeer.setDiscoveryPort(discoveryMemberChannelPort);
                    break;
                }
                log.info("Discovery MemberChannel port " + tcpMemberChannelPort + " is NOT available - incrementing");
                discoveryMemberChannelPort++;
            }

            discoveryChannel.startup();
        }

        start();
//        waitForJoin();
//        printMemberList();
    }

    private void waitForJoin() {
        if (assumeJoined) {
            return;
        }
        synchronized (joinNotify) {
            try {
                joinNotify.wait(HEARTBEAT_DELAY);
            } catch (InterruptedException e) {
            }
        }
    }


    public boolean isMaster() {
        List<Member> list = getSortedMembers(true);
        Member _master = null;
        UDPBroadCastingChannel _discoveryChannel = (UDPBroadCastingChannel) discoveryChannel;

        if (list.size() >= MIN_MEMBERS_FOR_MASTER_SELECTION) {
            _master = list.get(0);
            if (_discoveryChannel != null) {
                _discoveryChannel.disableActivePolling();
            }
            if (_master.equals(self)) {
                if (!_master.equals(lastMaster)) {
                    log.info("master_select: I have become master, replacing " + lastMaster);
                    lastMaster = _master;
                }
                return true;
            } else {
                if (lastMaster == null) {
                    log.info("master_select: I am not master - master is  " + _master);
                    lastMaster = _master;
                } else {
                    if (!_master.equals(lastMaster)) {
                        log.info("master_select: New master selected (not me),  " + _master + " replaces " + lastMaster);
                        lastMaster = _master;
                    }
                }
                return false;
            }
        }

        if (maxMemberCount > 1 && maySelectMasterOnPartition) {
            _master = list.get(0);
            if (!_master.equals(lastMaster)) {
                log.info("master_select: I have become sole master - post partition, replacing " + lastMaster);
                lastMaster = _master;
                if (_discoveryChannel != null) {
                    _discoveryChannel.enableActivePolling();
                }
            }
            return true;
        }

        if (discoveryChannel != null) {
            UDPBroadCastingChannel chan = (UDPBroadCastingChannel) discoveryChannel;
            chan.enableActivePolling();
        }

        log.info("master_select: Not enough members for master selection, there are " + list.size() + " and " + MIN_MEMBERS_FOR_MASTER_SELECTION + " are required");
        return false;
    }

    public Member getSelf() {
        return self.copy();
    }

    public List<Member> getSortedMembers(boolean includeSelf) {
        List<Member> sorted = Lists.newArrayList(otherMembers.values());
        if (includeSelf) {
            sorted.add(self);
        }

        if (sorted.size() > maxMemberCount) {
            maxMemberCount = sorted.size();
        }

        Collections.sort(sorted, new Comparator<Member>() {
            public int compare(Member o1, Member o2) {
                if (o1.getStartedTs() == o2.getStartedTs()) {
                    // compare names - this is possible
                    return o1.getId().compareTo(o2.getId());
                } else {
                    // compare timestamps
                    if (o1.getStartedTs() > o2.getStartedTs()) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            }
        });

        return sorted;
    }

    private Member generateSelf() throws Exception {
        Member member = new Member();
        member.setId(gridName + "-" + NetworkUtilities.getLocalShortHostName() + "-" + RandomUtil.getRandomAlpha(12));
        member.setPeer(localPeer);
        member.setStartedTs(System.currentTimeMillis());
        member.setGridName(gridName);

        localPeer.setAddress(NetworkUtilities.getLocalIPAddressOnSubnet(configSource.getStringValue("use.subnet")));
        localPeer.setMemberId(member.getId());

        return member;
    }

    public void leave() {
        notifier.shutdown();
        keepRunning.stopRunning();
        notifier.removeAllListeners();
        receiveQueue.clear();
        memberChannel.close();
        commChannel.close();
    }

    private Member lookupMember(String creatorID) {
        return otherMembers.get(creatorID);
    }

    private void sendReceiveConfirmation(NetworkMessage packet) throws Exception {
        ReceiveConfirmation confirm = new ReceiveConfirmation();
        Member from = lookupMember(packet.getCreator());
        confirm.oid = packet.getObjectId();
        confirm.creator = packet.getCreator();
        confirm.member = self;

        if (from != null)
            sendAsync(confirm, from);
        else
            sendAsync(confirm);
    }


    public int sendAsync(Object o) {
        return sendAsync(o, new ArrayList());
    }

    public int sendAsync(Object o, Member member) {
        List<Member> list = new ArrayList();
        list.add(member);
        return sendAsync(o, list);
    }

    public int sendAsync(Object o, List<Member> targetMembers) {
        PacketInfo info;
        ApplicationMessage amessage = new ApplicationMessage();
        amessage.setTargetMembers(targetMembers);
        amessage.setOriginMember(self);
        amessage.setObject(o);

        if (o instanceof MGroupMessage) {
            info = memberChannel.sendAsync(self, amessage);
        } else {
            info = commChannel.sendAsync(self, amessage);
        }

        StatisticsRecorder.gi().increment(gridName + Constants.CTR_MESSAGES_SENT);
        return info.bytesOut;
    }


    private void notifyListeners(Object o) {
        notifier.notifyListeners("event", o);
    }

    public void registerListener(MGroupListener listener) {
        notifier.addListener(listener);
    }

    public void deRegisterListener(MGroupListener listener) {
        notifier.removeListener(listener);
    }

    public void run() {
        Object o;
        NetworkMessage p;
        ApplicationMessage appMessage;

        MGroupListener listener;
        log.debug("[LiteGrid] Starting - " + gridName);
        boolean discard = false;

        try {
            memberChannel.sendAsync(self, new MemberPoll(self.getId(), true));

            while (keepRunning.shouldKeepRunning()) {
                discard = false;
                p = (NetworkMessage) receiveQueue.poll(HEARTBEAT_DELAY, TimeUnit.MILLISECONDS);

                if (!assumeJoined) {
                    assumeJoined = true;
                    synchronized (joinNotify) {
                        joinNotify.notifyAll();
                    }
                }

                if (System.currentTimeMillis() - lastHeartbeat > HEARTBEAT_DELAY) {
                    if (!REDUCE_MEMBER_POLLING) {
                        memberChannel.sendAsync(self, new MemberPoll(self.getId()));
                    } else {
                        memberChannel.sendAsync(self, self);
                    }
                    lastHeartbeat = System.currentTimeMillis();
                    checkMemberList();
                }
                if (p == null)
                    continue;

                StatisticsRecorder.gi().increment(gridName + Constants.CTR_MESSAGES_RECEIVED);

                if (p.getObject() instanceof ApplicationMessage) {
                    appMessage = (ApplicationMessage) p.getObject();
                    o = appMessage.getObject();

                    if (appMessage.getTargetMembers().size() > 0) {
                        if (appMessage.getTargetMembers().contains(self)) {
                            discard = false;
                        } else {
                            discard = true;
                        }
                    }
                } else {
                    o = p.getObject();
                }

                if (discard) {
                    StatisticsRecorder.gi().increment(gridName + Constants.CTR_MESSAGES_DISCARDED);
                    continue;
                }

                if (o instanceof MGroupMessage) {
                    handleMGroupMessage((MGroupMessage) o);
                    isMaster();
                    continue;
                }

                if (p.getConfirmationRequired()) {
                    sendReceiveConfirmation(p);
                }

                notifyListeners(o);
            }
        } catch (Exception e) {
            if (keepRunning.shouldKeepRunning()) {
                log.error("Exception in run method state - " + this, e);
            }
        }
        log.debug("Leaving litegrid " + gridName);


    }

    @Override
    public String toString() {
        return "LiteGrid{" +
                ", receiveQueue=" + receiveQueue +
                ", gridName='" + gridName + '\'' +
                ", otherMembers=" + otherMembers +
                '}';
    }

    private void checkMemberList() throws Exception {
        ArrayList<Member> removedMembers = new ArrayList();
        Object[] keys = otherMembers.keySet().toArray();
        MemberLeaveEvent event;
        Member member;

        for (int i = 0; i < keys.length; i++) {
            member = otherMembers.get(keys[i]);
            if (System.currentTimeMillis() - member.getLastHeartbeat() > EXPIRE_AFTER) {
                log.debug("Member  " + keys[i].toString() + " not responded. will remove from member list");
                otherMembers.remove(keys[i]);
                fragmentManager.removeObjectsFromCreator(member.getId());
                removedMembers.add(member);
                log.debug("OLD " + member.toString());
            }

        }

        for (int i = 0; i < removedMembers.size(); i++) {
            event = new MemberLeaveEvent();
            event.setMember(removedMembers.get(i));
            notifyListeners(event);
        }

        isMaster();

    }

    private synchronized void handleMGroupMessage(MGroupMessage message) throws Exception {
        if (message instanceof Member) {
            Member receivedMember = (Member) message;
            Member existingMember = otherMembers.get(receivedMember.getId());
            MemberJoinEvent joinEvent;

            if (receivedMember.getId().equals(self.getId())) {
                return;
            }

            if (existingMember == null) {
                receivedMember.onHeartbeat();
                //log.debug("New  " + member);
                otherMembers.put(receivedMember.getId(), receivedMember);
                joinEvent = new MemberJoinEvent();
                joinEvent.member = receivedMember;
                commChannel.connect(receivedMember.getPeer());
                notifyListeners(joinEvent);
                printMemberList();
            } else {
                existingMember.onHeartbeat();
                //log.debug("Existing Member Alive " + member);
            }
        } else if (message instanceof BroadcastedMemberPoll) {
            // special case where we will need to reply via the discovery channel

            if (discoveryChannel == null) {
                throw new RuntimeException("Received a BroadcastedMemberPoll - but not discoveryChannel present");
            }

            BroadcastedMemberPoll pollReq = (BroadcastedMemberPoll) message;


            if (self.getId().equals(pollReq.getFrom())) {
                log.info("Ignoring BroadcastedMemberPoll poll from self");
                return;
            }
            // fudge the return address and port;
            log.debug("Received BroadcastedMemberPoll from " + pollReq.getFrom() + " - requesting broadcast? " + pollReq.isRequestPeerBroadcast());

            Member fudgeMember = new Member();
            fudgeMember.setId("fudge-" + UIDUtil.getUid());
            Peer fudgePeer = new Peer();
            fudgeMember.setPeer(fudgePeer);
            fudgePeer.setMemberId(fudgeMember.getId());
            fudgePeer.setAddress(pollReq.getReturnHost());
            fudgePeer.setDiscoveryPort(pollReq.getReturnPort());

            PeerBroadcast broadcast = new PeerBroadcast();
            broadcast.setMember(self);
            broadcast.setMembers(getSortedMembers(true));

            log.debug("Sending PeerBroadcast with " + broadcast.getMembers().size() + " to " + fudgePeer);

            discoveryChannel.sendAsync(fudgeMember, broadcast);

        } else if (message instanceof MemberPoll) {
            MemberPoll poll = (MemberPoll) message;
            log.debug("Received MemberPoll from " + poll.getFrom() + " - requesting broadcast? " + poll.isRequestPeerBroadcast());

            if (poll.isRequestPeerBroadcast()) {
                PeerBroadcast broadcast = new PeerBroadcast();
                broadcast.setMember(self);
                broadcast.setMembers(getSortedMembers(true));
                log.debug("Sending peerBroadcast out");
                sendAsync(broadcast);
            } else {
                log.debug("Sending self out");
                sendAsync(self);
            }
        } else if (message instanceof PeerBroadcast) {
            PeerBroadcast peerBroadcast = (PeerBroadcast) message;

            log.debug("Received peerBroadcast from " + peerBroadcast.getMember());

            if (discoveryChannel != null) {
                UDPBroadCastingChannel chan = (UDPBroadCastingChannel) discoveryChannel;
                chan.disableActivePolling();
            }

            log.debug("Notifying member channel of " + peerBroadcast.getMembers() + " potential peers");

            for (Member member : peerBroadcast.getMembers()) {
                log.debug("Potential Peer: "+ member);
            }

            memberChannel.notifyOfPotentialPeers(peerBroadcast);

        }


    }

    private void printMemberList() {
        String s = "[LiteGrid] (" + gridName + ") ";
        Object[] keys = otherMembers.keySet().toArray();

        s += "Self (" + self.getId() + ") Others [";

        for (int i = 0; i < keys.length; i++) {
            s += keys[i] + ",";
        }
        s += "]";
        log.debug(s);
    }

}
