package com.mac.litegrid.clustering.communication.interfaces;

import com.mac.litegrid.clustering.communication.messages.Member;
import com.mac.litegrid.clustering.communication.messages.Peer;
import com.mac.litegrid.clustering.communication.messages.PeerBroadcast;
import com.mac.litegrid.clustering.communication.packets.PacketInfo;

/**
 * Created by IntelliJ IDEA.
 * User: mandrewes
 * Date: 11/01/2005
 * <p>
 * To change this template use File | Settings | File Templates.
 */
public interface MulticastChannel {
    int connect(Peer peer);

    void notifyOfPotentialPeers(PeerBroadcast broadcast);

    PacketInfo sendAsync(Member member, Object o);

    PacketInfo sendAsync(Member member, Object o, PacketInfo packetInfo, boolean requireConfirmation);

    void startup();

    void close();
}
