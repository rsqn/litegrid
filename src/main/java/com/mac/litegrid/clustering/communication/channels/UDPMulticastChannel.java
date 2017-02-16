package com.mac.litegrid.clustering.communication.channels;

import com.mac.libraries.concurrency.KeepRunning;
import com.mac.libraries.sequences.Sequence;
import com.mac.litegrid.clustering.communication.interfaces.MulticastChannel;
import com.mac.litegrid.clustering.communication.interfaces.NetworkMessage;
import com.mac.litegrid.clustering.communication.messages.Member;
import com.mac.litegrid.clustering.communication.messages.Peer;
import com.mac.litegrid.clustering.communication.messages.PeerBroadcast;
import com.mac.litegrid.clustering.communication.packets.FragmentedObjectPacket;
import com.mac.litegrid.clustering.communication.packets.PacketInfo;
import com.mac.litegrid.clustering.communication.workers.FlowController;
import com.mac.litegrid.clustering.communication.workers.FragmentManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by IntelliJ IDEA.
 * User: mandrewes
 * Date: 10/01/2005
 *
 * To change this template use File | Settings | File Templates.
 */
public class UDPMulticastChannel extends Thread implements MulticastChannel {
    protected Logger log = LoggerFactory.getLogger(getClass());
    private MulticastSocket socket;
    private InetAddress group;
    private KeepRunning keepRunning;
    private String address;
    private int port;
    private FragmentManager fragManager;
    private FlowController flowController;
    private static int packetLimitPerSecond = 500;
    private Member self;
    public enum ChannelType {MEMBERSHIP, COMMUNICATIONS}
    private ChannelType channelType;

    public static void setMaxUdpPacketLimitPerSecondSecond(int limit) {
        UDPMulticastChannel.packetLimitPerSecond = limit;
    }

    public void setFragmentManager(FragmentManager fragManager) {
        this.fragManager = fragManager;
    }

    @Override
    public int connect(Peer peer) {
        return -1;
    }

    public UDPMulticastChannel(ChannelType t, Member self, String address, int port, FragmentManager fragMgr) throws Exception {
        this.setDaemon(true);
        this.keepRunning = new KeepRunning();
        this.self = self;
        this.address = address;
        this.port = port;
        this.fragManager = fragMgr;
        this.channelType = t;
        this.flowController = new FlowController(packetLimitPerSecond);

        log.info("UDPChannel.address=" + address + ",port=" + port);
        this.socket = new MulticastSocket(port);
        this.group = InetAddress.getByName(address);
        this.socket.joinGroup(group);

//        localPeer.setAddress(NetworkUtilities.getLocalIPAddressOnSubnet(configSource.getStringValue("use.subnet")));


        log.info("Multicast initial buffer " + socket.getReceiveBufferSize());
        this.socket.setReceiveBufferSize(socket.getReceiveBufferSize() * 128);
        log.info("Multicast started on " + port + " buffer " + socket.getReceiveBufferSize());
    }

    @Override
    public void startup() {
        start();
    }
//
//    public NetworkMessage getPacketForSyncSend(Member member, Object o) throws Exception {
//        FragmentedObjectPacket packet = new FragmentedObjectPacket();
//        packet.setObject(member.getId(), Sequence.nextLong(), o);
//        return packet;
//    }
//
//    public PacketInfo getNextPacketInfo(Member member)  {
//        PacketInfo ret = new PacketInfo();
//        ret.oid = Sequence.nextLong();
//        ret.creator = member.getId();
//        return ret;
//    }


    @Override
    public void notifyOfPotentialPeers(PeerBroadcast broadcast) {

    }

    public PacketInfo sendAsync(Member member, Object o)   {
        return sendAsync(member, o, null, false);
    }

    public PacketInfo sendAsync(Member member, Object o, PacketInfo info, boolean requireConfirmation)   {
        PacketInfo ret = new PacketInfo();
        FragmentedObjectPacket packet = new FragmentedObjectPacket();
        if (info == null)
            packet.setObject(member.getId(), Sequence.nextLong(), o);
        else
            packet.setObject(member.getId(), info.oid, o);

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

        while ((buff = packet.getNextFragment()).length != 0) {
            datagram = new DatagramPacket(buff, buff.length, group, port);
            try {
                socket.send(datagram);
            } catch (IOException e) {
                throw new RuntimeException("Exception in write " + e, e);
            }
        }

        return buff.length;
    }


    public void close()   {
        log.debug("Closing multicast");
        keepRunning.stopRunning();
        try {
            socket.close();
        } catch (Exception ignore) {

        }
    }


    public void run() {
        byte[] buff = new byte[64 * 6 + (FragmentedObjectPacket.MAX_SIZE * 2)];
        DatagramPacket datagram = new DatagramPacket(buff, buff.length);
        try {
            while (keepRunning.shouldKeepRunning()) {
                socket.receive(datagram);
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
