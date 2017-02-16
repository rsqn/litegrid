package com.mac.litegrid.clustering.communication.channels;

import com.mac.libraries.concurrency.Daemon;
import com.mac.libraries.concurrency.KeepRunning;
import com.mac.libraries.concurrency.SimpleDaemon;
import com.mac.libraries.configuration.ConfigurationSource;
import com.mac.libraries.events.GenericCallBack;
import com.mac.libraries.sequences.Sequence;
import com.mac.litegrid.clustering.communication.interfaces.MulticastChannel;
import com.mac.litegrid.clustering.communication.interfaces.NetworkMessage;
import com.mac.litegrid.clustering.communication.messages.ApplicationMessage;
import com.mac.litegrid.clustering.communication.messages.Member;
import com.mac.litegrid.clustering.communication.messages.Peer;
import com.mac.litegrid.clustering.communication.messages.PeerBroadcast;
import com.mac.litegrid.clustering.communication.packets.NormalObjectPacket;
import com.mac.litegrid.clustering.communication.packets.PacketInfo;
import com.mac.litegrid.clustering.communication.workers.LiteGrid;
import com.mac.litegrid.clustering.communication.workers.LiteGridManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: mandrewes
 * Date: 10/01/2005
 * <p>
 * To change this template use File | Settings | File Templates.
 */

/**
 * TODO: channelKey and tcp bind ports are not compatible with each other - need to resolve (though only really an issue on a single machine)
 */


public class TCPChannel extends Thread implements MulticastChannel {
    protected Logger log = LoggerFactory.getLogger(getClass());

    public enum ChannelType {MEMBERSHIP, COMMUNICATIONS}

    private List<TCPTransceiver> transceivers;
    private List<Peer> knownPeers;
    private BlockingQueue<Peer> discoveredPeers;

    private KeepRunning keepRunning;
    private ServerSocket serverSocket;
    private BlockingQueue receiveQueue;
    private Daemon connectorDaemon;

    private Member self;
    private ConfigurationSource config;
    private ChannelType channelType;
    private int serverPort;
    private boolean acceptIncomingConnections;

    public TCPChannel(ChannelType t, Member self, int serverPort, ConfigurationSource config, BlockingQueue receiveQueue) throws Exception {
        this.setDaemon(true);
        this.self = self;
        this.keepRunning = new KeepRunning();
        this.serverPort = serverPort;
        this.config = config;
        this.channelType = t;
        this.receiveQueue = receiveQueue;
        this.acceptIncomingConnections = true;
        this.transceivers = new ArrayList<>();
        this.knownPeers = new ArrayList<>();
        this.discoveredPeers = new ArrayBlockingQueue<Peer>(500);

        loadPreConfiguredPeers(this.channelType);

        log.info(channelType + " Attempting to bind ServerSocket on " + config.getStringValue("tcp.bind.address", "0.0.0.0") + ":" + serverPort);
        this.serverSocket = new ServerSocket();
        try {
            serverSocket.bind(new InetSocketAddress(config.getStringValue("tcp.bind.address", "0.0.0.0"), serverPort), 50);
            log.info(channelType + " Bound ServerSocket on " + config.getStringValue("tcp.bind.address", "0.0.0.0") + ":" + serverPort);
        } catch (BindException e) {
            if (config.getBoolValue("tcp.ignore.socket.bind.failure", true)) {
                acceptIncomingConnections = false;
                log.info(channelType + " Unable to bind ServerSocket on " + config.getStringValue("tcp.bind.address", "0.0.0.0") + ":" + serverPort
                        + " This is ok, may be more than one group running in this node, routed connection should take care of this");
            } else {
                log.info(channelType + " Unable to bind ServerSocket on " + config.getStringValue("tcp.bind.address", "0.0.0.0") + ":" + serverPort
                        + " tcp.ignore.socket.bind.failure=false " + e.getMessage());
                throw e;
            }
        }
    }

    /**
     * This is really only needed when running locally
     *
     * @param t
     */
    public static boolean isPortAvailable(int port) {

        ServerSocket ss = null;
//        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
//            ds = new DatagramSocket(port);
//            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
//            if (ds != null) {
//                ds.close();
//            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                /* should not be thrown */
                }
            }
        }

        return false;
    }


    private void loadPreConfiguredPeers(ChannelType t) {
        if (t.equals(ChannelType.MEMBERSHIP)) {
            for (String key : config.asMap().keySet()) {
                if (key.startsWith("tcp.membership.endpoint")) {
                    log.info(channelType + " TCP Configured Peer " + config.getStringValue(key));
                    String[] values = config.getStringValue(key).split(":");
                    String ip = values[0];
                    String channelKey = "";

                    if (values.length > 1) {
                        channelKey = values[1];
                    }

                    int memberChannelPort = LiteGridManager.getUniquePort(self.getGridName() + channelKey);
                    int commChannelPort = LiteGridManager.getUniquePort(self.getGridName() + channelKey + LiteGrid.COMM_CHANNEL_SUFFIX);

                    Peer m = new Peer();
                    m.setMemberId(ip);
                    m.setAddress(ip);
                    m.setTcpMemberChannelPort(memberChannelPort);
                    m.setTcpCommsChannelPort(commChannelPort);
                    m.setRetryIntervalMs(config.getIntValue("tcp.membership.retry.interval.ms", 15000));
                    m.setRetryExponent(config.getIntArray("tcp.membership.retry.interval.exponents", ","));
                    knownPeers.add(m);
                }
            }
        }
        log.info(channelType + " Loaded " + this.knownPeers.size() + " knownPeers that I will initiate connections to");
    }

    @Override
    public void notifyOfPotentialPeers(PeerBroadcast broadcast) {

        for (Member member : broadcast.getMembers()) {
            if (member.getId().equals(self.getId())) {
                continue;
            }

            Peer peer = member.getPeer();
            peer.setRetryIntervalMs(config.getIntValue("tcp.membership.retry.interval.ms", 15000));
            peer.setRetryExponent(config.getIntArray("tcp.membership.retry.interval.exponents", ","));
            try {
                discoveredPeers.put(peer);
            } catch (InterruptedException e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    private TCPTransceiver resolveForPeer(Peer peer) {
        synchronized (transceivers) {
            for (TCPTransceiver tranceiver : transceivers) {
                if (tranceiver.matchesRemotePeer(peer)) {
                    return tranceiver;
                }
            }
        }
        return null;
    }

    private TCPTransceiver resolveForMember(Member member) {
        synchronized (transceivers) {
            for (TCPTransceiver tranceiver : transceivers) {
                if (tranceiver.matchesRemoteMember(member)) {
                    return tranceiver;
                }
            }
        }
        return null;
    }

    private void addTranceiver(TCPTransceiver t) {
        synchronized (transceivers) {
            transceivers.add(t);
        }
    }

    private boolean removeTranceiver(TCPTransceiver t) {
        synchronized (transceivers) {
            return transceivers.remove(t);
        }
    }

    private Set<Member> resolveMembersFromConnectionTranceivers() {
        Set<Member> ret = new HashSet<>();
        synchronized (transceivers) {
            for (TCPTransceiver transceiver : transceivers) {
                if (transceiver.getRemoteMember() != null) {
                    ret.add(transceiver.getRemoteMember());
                }
            }
        }
        return ret;
    }


    public int connect(final Peer peer) {
        try {
            TCPTransceiver tranceiver = null;

            GenericCallBack successCb = new GenericCallBack() {
                @Override
                public void onCallBack(Object o) {
                    log.info(channelType + " onReady for " + peer);
                }
            };

            GenericCallBack errorCb = new GenericCallBack<TCPTransceiver>() {
                @Override
                public void onCallBack(TCPTransceiver t) {
                    boolean b = removeTranceiver(t);
                    log.info(channelType + " onError " + peer + " removed from list? " + b);

                }
            };

            tranceiver = resolveForPeer(peer);
            if (tranceiver != null) {
                log.info(channelType + " for " + peer + " found ready = " + tranceiver.isReady());
                return 1;
            } else {
                log.info(channelType + " for " + peer + " NOT found");
            }

            if (peer != null) {
                peer.setLastConnectionAttempt(System.currentTimeMillis());
            }

            log.info("Connecting to " + peer);
            Socket s = new Socket(peer.getAddress(), getTargetPort(peer));
            tranceiver = new TCPTransceiver(self, s, receiveQueue, successCb, errorCb, config);
            addTranceiver(tranceiver);
            tranceiver.start();


        } catch (Exception e) {
            peer.onFailedConnection();
            log.error(channelType + " failed connection " + e.getMessage(), e);
        }
        return 1;
    }

    public PacketInfo sendAsync(Member member, Object message) {
        return sendAsync(member, message, null, false);
    }

    public PacketInfo sendAsync(Member member, Object message, PacketInfo info, boolean requireConfirmation) {
        PacketInfo ret = new PacketInfo();
        NetworkMessage packet = new NormalObjectPacket();
        if (info == null)
            packet.setObject(member.getId(), Sequence.nextLong(), message);
        else
            packet.setObject(member.getId(), info.oid, message);
        packet.setConfirmationRequired(requireConfirmation);

        ret.oid = packet.getObjectId();
        ret.creator = packet.getCreator();
        ret.bytesOut = 0;
//        ret.bytesOut += write(member, packet);
        Set<Member> targets = new HashSet();


        if (message instanceof ApplicationMessage) {
            ApplicationMessage appMsg = (ApplicationMessage) message;
            for (Member targetMember : appMsg.getTargetMembers()) {
                targets.add(targetMember);
            }
        }

        if (targets.size() == 0) {
            targets.addAll(resolveMembersFromConnectionTranceivers());
        }
        targets.remove(self);

        for (Member target : targets) {
            ret.bytesOut += write(target, packet);
        }

        return ret;
    }

    private int getTargetPort(Peer peer) {
        if (channelType == ChannelType.MEMBERSHIP) {
            return peer.getTcpMemberChannelPort();
        } else {
            return peer.getTcpCommsChannelPort();
        }
    }

    private synchronized int write(Member member, NetworkMessage packet) {
        if (member.equals(self)) {
            return 0;
        }
        TCPTransceiver transceiver = resolveForMember(member);
        if (transceiver == null || (!transceiver.isReady())) {
            return -1;
        }
        transceiver.write(packet);
        return 1;
    }


    public void close() {
        keepRunning.stopRunning();
        connectorDaemon.shutdown();
        try {
            serverSocket.close();
        } catch (Exception ignore) {

        }
        synchronized (transceivers) {
            for (TCPTransceiver t : transceivers) {
                t.close();
            }
        }
    }

    @Override
    public void startup() {
        start();
    }

    public void run() {
        Socket socket;
        String address;
        int port;

        connectorDaemon = new SimpleDaemon()
                .withFixedDelay(config.getIntValue("tcp.retry.interval.ms", 15000))
                .andRunnable(
                        new Runnable() {
                            @Override
                            public void run() {
                                List<Peer> copy = new CopyOnWriteArrayList<>(knownPeers);
                                for (Peer configuredPeer : copy) {
                                    TCPTransceiver t = resolveForPeer(configuredPeer);
                                    if (t == null) {
                                        log.debug(channelType + " not connected to " + configuredPeer);
                                        if (configuredPeer.calculateNextRetry() < System.currentTimeMillis()) {
                                            if (0 == connect(configuredPeer)) {
                                                configuredPeer.onSuccessfullConnection();
                                            } else {
                                                configuredPeer.onFailedConnection();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                );
        connectorDaemon.start();


        Thread t = new Thread() {
            @Override
            public void run() {
                boolean firstRun = true;
                while (keepRunning.shouldKeepRunning()) {
                    try {
                        Peer peer = discoveredPeers.poll(2000, TimeUnit.MILLISECONDS);
                        if (peer != null) {
                            TCPTransceiver t = resolveForPeer(peer);
                            if (t == null) {
                                log.debug(channelType + " not connected to " + peer);
                                if (peer.calculateNextRetry() < System.currentTimeMillis()) {
                                    if (0 == connect(peer)) {
                                        peer.onSuccessfullConnection();
                                    } else {
                                        peer.onFailedConnection();
                                    }
                                }
                            }
                        }
                    } catch (Exception ignore) {
                        log.warn("Exception in discovery spammer " + ignore.getMessage(), ignore);
                    }
                }
            }
        };
        t.setDaemon(true);
        t.start();

        if (!acceptIncomingConnections) {
            log.info(channelType + " Not accepting socket connections - creating outbound sockets only");
            try {
                while (keepRunning.shouldKeepRunning()) {
                    keepRunning.wait(60000L);
                }
            } finally {
                return;
            }
        }

        log.debug(channelType + " TCP Server Socket Opened on serverPort " + this.serverPort + " for " + this.channelType);
        try {
            while (keepRunning.shouldKeepRunning()) {
                socket = serverSocket.accept();
                address = socket.getInetAddress().toString();
                port = socket.getPort();

                GenericCallBack successCb = new GenericCallBack<TCPTransceiver>() {
                    @Override
                    public void onCallBack(TCPTransceiver t) {
//                        log.info("TCPChannel onSuccess {}", t);
//                        TCPTransceiver prior = resolveForMember(t.getRemoteMember());
//                        if (prior != null) {
//                            if (prior.isReady()) {
//                                log.error(channelType + " Socket is already open to " + t.getRemoteMember().getPeer() + " dropping");
//                                t.close();
//                                return;
//                            } else {
//                                log.error(channelType + " Socket previously existing to " + t.getRemoteMember().getPeer() + " but is closed");
//                                removeTranceiver(prior);
//                            }
//                        }
//                        log.error(channelType + " Completed accepting request to " + t.getRemoteMember().getPeer());
                    }
                };

                GenericCallBack errorCb = new GenericCallBack<TCPTransceiver>() {
                    @Override
                    public void onCallBack(TCPTransceiver t) {
                        boolean b = removeTranceiver(t);
                        log.info(channelType + " onError " + t.getRemoteMember() + " removed from list " + b);
                    }
                };

                log.debug(channelType + " Accepted TCP Connection from " + address + ":" + port);
                TCPTransceiver tranceiver = new TCPTransceiver(self, socket, receiveQueue, successCb, errorCb, config);
                addTranceiver(tranceiver);
                tranceiver.start();
            }
        } catch (Exception e) {
            if (keepRunning.shouldKeepRunning()) {
                log.error(e.getMessage(), e);
            }
        }
        log.debug(channelType + " Channel stopped");
    }


}

