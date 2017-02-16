package com.mac.litegrid.clustering.communication.channels;

import com.mac.libraries.concurrency.KeepRunning;
import com.mac.libraries.configuration.ConfigurationSource;
import com.mac.libraries.generic.UIDUtil;
import com.mac.libraries.sequences.Sequence;
import com.mac.litegrid.clustering.communication.interfaces.MulticastChannel;
import com.mac.litegrid.clustering.communication.interfaces.NetworkMessage;
import com.mac.litegrid.clustering.communication.messages.*;
import com.mac.litegrid.clustering.communication.packets.FragmentedObjectPacket;
import com.mac.litegrid.clustering.communication.packets.PacketInfo;
import com.mac.litegrid.clustering.communication.workers.FlowController;
import com.mac.litegrid.clustering.communication.workers.FragmentManager;
import com.mac.litegrid.clustering.communication.workers.LiteGridManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;


/**
 * This channel is only used for discovery
 */

public class UDPBroadCastingChannel extends Thread implements MulticastChannel {
    protected Logger log = LoggerFactory.getLogger(getClass());
    private DatagramSocket socket;
    private KeepRunning keepRunning;
    private int port;
    private FragmentManager fragManager;
    private FlowController flowController;
    private static int packetLimitPerSecond = 500;
    private Member self;

    public enum ChannelType {MEMBERSHIP}

    private boolean activePolling = true;
    private ChannelType channelType;
    private ConfigurationSource config;

    public static void setMaxUdpPacketLimitPerSecondSecond(int limit) {
        UDPBroadCastingChannel.packetLimitPerSecond = limit;
    }

    public void setFragmentManager(FragmentManager fragManager) {
        this.fragManager = fragManager;
    }

    @Override
    public int connect(Peer peer) {
        return -1;
    }

    private boolean shouldPoll() {
        return activePolling;
    }

    public UDPBroadCastingChannel(ChannelType t, Member self, String address, int port, FragmentManager fragMgr, ConfigurationSource config) throws Exception {
        this.setDaemon(true);
        this.keepRunning = new KeepRunning();
        this.self = self;
        this.port = port;
        this.fragManager = fragMgr;
        this.channelType = t;
        this.flowController = new FlowController(packetLimitPerSecond);
        this.activePolling = true;
        this.config = config;

        log.info("UDPBroadcastingChannel.address=" + address + ",port=" + port);
        this.socket = new DatagramSocket(port);
        log.info("Datagram initial buffer " + socket.getReceiveBufferSize());
        this.socket.setReceiveBufferSize(socket.getReceiveBufferSize() * 128);
        log.info("Datagram started on " + port + " buffer " + socket.getReceiveBufferSize());
    }

    @Override
    public void startup() {
        start();
    }

    @Override
    public void notifyOfPotentialPeers(PeerBroadcast broadcast) {

    }

    public PacketInfo sendAsync(Member member, Object o) {
        return sendAsync(member, o, null, false);
    }

    /**
     * This is really only needed when running locally
     *
     * @param t
     */
    public static boolean isPortAvailable(int port) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(port);
            return true;
        } catch (IOException e) {
        } finally {
            if (socket != null) {
                socket.close();
            }
        }

        return false;
    }

    public void disableActivePolling() {
        if ( activePolling ) {
            log.info("active polling disabled");
        }
        this.activePolling = false;
    }

    public void enableActivePolling() {
        if ( ! activePolling ) {
            log.info("active polling enabled");
        }
        this.activePolling = true;
    }

    public PacketInfo sendAsync(Member member, Object o, PacketInfo info, boolean requireConfirmation) {
        PacketInfo ret = new PacketInfo();
        FragmentedObjectPacket packet = new FragmentedObjectPacket();

        if (info == null) {
            packet.setObject(self.getId(), Sequence.nextLong(), o);
        } else {
            packet.setObject(self.getId(), info.oid, o);
        }
        packet.setConfirmationRequired(requireConfirmation);

        ret.oid = packet.getObjectId();
        ret.creator = packet.getCreator();
        ret.bytesOut = write(member, packet);
        return ret;
    }

    public int sendAsync(Member member, NetworkMessage packet) throws Exception {
        //FragmentedObjectPacket packet = new FragmentedObjectPacket();
        //packet.setObject(member.id, badSeq++, o);
        return write(member, (FragmentedObjectPacket) packet); // just let it throw a classcast if someone passes in the wrong thing for now
    }

    private synchronized int write(Member member, FragmentedObjectPacket packet) {
        if (fragManager == null)
            throw new RuntimeException("No Fragment Manager set");
        flowController.controlMyFlow();
        DatagramPacket datagram;
        byte[] buff;

        InetAddress address = null;
        try {
            address = InetAddress.getByName(member.getPeer().getAddress());
        } catch (UnknownHostException e) {

            throw new RuntimeException("Exception resolving host for discovery channel response " + e.getMessage(), e);
        }

        log.debug("Sending UDP packet to " + member.getPeer().getAddress() + ":" + member.getPeer().getDiscoveryPort());
        while ((buff = packet.getNextFragment()).length != 0) {
            datagram = new DatagramPacket(buff, buff.length, address, member.getPeer().getDiscoveryPort());
            try {
                socket.send(datagram);
            } catch (IOException e) {
                log.debug("Exception in UDP spamming write - ignoring " + e.getMessage());
//                throw new RuntimeException("Exception in write " + e, e);
            }
        }

        return buff.length;
    }


    public void close() {
        log.debug("Closing multicast");
        keepRunning.stopRunning();
        try {
            socket.close();
        } catch (Exception ignore) {

        }
    }


    private void scanRanges(ChannelType t) {
        if (t.equals(ChannelType.MEMBERSHIP)) {
            String rangeConfig = config.getStringValue("udp_spamming.broadcast.range");

            String base = rangeConfig.substring(0, rangeConfig.lastIndexOf("."));
            String rangeStr = rangeConfig.substring(rangeConfig.lastIndexOf(".") + 1);
            String[] parts = rangeStr.split("-");

            int rangeStart = Integer.parseInt(parts[0]);
            int rangeEnd = Integer.parseInt(parts[1]);

            int discoveryPort = LiteGridManager.getUniquePort(self.getGridName());

            log.info(channelType + " UDP Scanning Peers " + rangeConfig);
            log.info("Scanning " + base + "." + rangeStart + " to " + base + "." + rangeEnd + " on port " + discoveryPort);

            for (int i = rangeStart; i <= rangeEnd; i++) {
                String target = base + "." + i;

                Peer fakePeer = new Peer();
                fakePeer.setAddress(target);
                fakePeer.setDiscoveryPort(discoveryPort);

                Member fakeMember = new Member();
                fakeMember.setId("fake-" + UIDUtil.getUid());
                fakeMember.setPeer(fakePeer);
                fakePeer.setMemberId(fakeMember.getId());

                BroadcastedMemberPoll poll = new BroadcastedMemberPoll();
                poll.setRequestPeerBroadcast(true);
                poll.setFrom(self.getId());
                poll.setReturnHost(self.getPeer().getAddress());
                poll.setReturnPort(port);
                sendAsync(fakeMember, poll);

            }
        }
    }


    public void run() {
        byte[] buff = new byte[64 * 6 + (FragmentedObjectPacket.MAX_SIZE * 2)];
        DatagramPacket datagram = new DatagramPacket(buff, buff.length);

        Thread t = new Thread() {
            @Override
            public void run() {
                boolean firstRun = true;

                while (keepRunning.shouldKeepRunning()) {
                    try {
                        if (firstRun) {
                            firstRun = false;
                            keepRunning.doWait(1000);
                        } else {
                            keepRunning.doWait(config.getIntValue("udp_spamming.interval.ms", 30000));
                        }
                        if (shouldPoll()) {
                            scanRanges(channelType);
                        }
                    } catch (Exception ignore) {
                        log.warn("Exception in discovery spammer " + ignore.getMessage(), ignore);
                    }
                }
            }
        };
        t.setDaemon(true);
        t.start();

        try {
            while (keepRunning.shouldKeepRunning()) {
                socket.receive(datagram);
                log.debug("UDPBroadcastingChannel Received a datagram from " + datagram.getAddress().toString());

                try {
                    if (fragManager == null)
                        throw new Exception("No Fragment Manager set");
                    fragManager.receiveFragment(datagram.getData());
                } catch (Exception e) {
                    if (keepRunning.shouldKeepRunning()) {
                        log.error("Exception receiving data fragment ");
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            if (keepRunning.shouldKeepRunning()) {
                log.error(e.getMessage(), e);
            }
        }

        log.debug("closing multicast socket");
        try {
            socket.close();
        } catch (Exception e) {

        }

    }


}
