package com.mac.litegrid.clustering.communication.workers;


import com.mac.litegrid.clustering.communication.messages.Member;
import com.mac.litegrid.clustering.communication.messages.UnableToDeserializeError;
import com.mac.litegrid.clustering.communication.messages.UnableToDeserializePacket;
import com.mac.litegrid.clustering.communication.packets.FragmentedObjectPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InvalidClassException;
import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;


/**
 * Created by IntelliJ IDEA.
 * User: mandrewes
 * Date: 11/01/2005
 *
 * To change this template use File | Settings | File Templates.
 */
public class FragmentManager {
    protected Logger log = LoggerFactory.getLogger(getClass());
    private Hashtable packets;
    private BlockingQueue queue;
    //private BlockingQueue internalPacketQueue;
    private Member self;
    private static final long DISCARD_PACKET_PERIOD = 1000 * 60; // if we havent got the whole packet within 60 second, drop it.
    private long lastHousekeeping = 0;


    public FragmentManager(Member self) {
        lastHousekeeping = System.currentTimeMillis();
        this.self = self;
        packets = new Hashtable();
        // start called from LiteGrid.
    }


    public void setQueue(BlockingQueue queue) {
        this.queue = queue;
        //internalPacketQueue = new BlockingQueue();
        // start called from LiteGrid.
    }

    // TODO, if a member comes on during a transfer, that object will never leave.. need a kind of (Confirm finished send)
    public void removeObjectsFromCreator(String creator) throws Exception {
        Object[] keys = packets.keySet().toArray();
        String key;
        //FragmentedObjectPacket packet;
        int count = 0;


        for (int i = 0; i < keys.length; i++) {
            key = (String) keys[i];
            if (key.startsWith(creator)) {
                /*
                packet = (FragmentedObjectPacket) packets.get(key);
                if (packet != null) {
                    log.logDevelopment(packet.describeMissingFragments());
                }
                */
                packets.remove(key);
                count++;
            }
        }

        if (count > 0)
            log.debug("removed " + count + " objects");

    }

    /*
    public void queueFragment(byte[] buff) throws Exception {
        internalPacketQueue.enQueue(buff);
    }


    public void requestShutdown() {
        internalPacketQueue.cancelQueue();
    }


    public void run() {
        while (true) {
            try {
                //buff = (byte[]) internalPacketQueue.deQueue();
                receiveFragment((byte[])internalPacketQueue.deQueue());
            } catch (QueueCancelledException qe) {
                break;
            } catch ( Exception e ) {
                System.out.println(e);
                // where do I put this?  This is to help packet loss due to slower machines, so rethrowing may
                // not be necessary, as at the moment slower machines can lose packets..
            }
        }
        log.info("Fragment manager exiting");
    }
    */

    public void receiveFragment(byte[] buff) throws Exception {
        FragmentedObjectPacket packet;
        ByteArrayInputStream bstream = new ByteArrayInputStream(buff);
        DataInputStream istream = new DataInputStream(bstream);

        String creator = istream.readUTF();
        long oid = istream.readLong();
        int security = istream.readInt();
        istream.close();
        //return new int[] {creator,oid,numfrags};
//        return new long[]{creator, oid, security};

//        long[] coid = FragmentedObjectPacket.getCreatorAndOIDAndSecurity(buff);
//        long creator = coid[0];
//        long oid = coid[1];
//        long security = coid[2]; // used to assist with separating multicast traffic when multiple otherMembers join using ID 1

//        if (creator == self.id && creator != LiteGrid.INITIAL_MEMBER_ID) { // 1 is the first ID available
//            return;
//        }
        if ( creator.equals(self.getId())) {
            return;
        }

        String key = "" + creator + ":" + oid + ":" + security;
        //System.out.println("packet key " + key);
        packet = (FragmentedObjectPacket) packets.get(key);

        if (packet == null) {
            packet = new FragmentedObjectPacket();
            packet.timestamp = System.currentTimeMillis();
            synchronized (packets) { // will get a concurrent modification exception if iterating and adding/removiing
                packets.put(key, packet);
                if (System.currentTimeMillis() - lastHousekeeping > DISCARD_PACKET_PERIOD) {
                    performHousekeeping();
                }
            }
        }

        try {
            packet.receiveFragment(buff);

            if (packet.numFragments == packet.numFragmentsReceived) {
                synchronized (packets) {
                    packets.remove(key);
                }
                try {
                    queue.add(packet);
                } catch (Exception e) {
                    log.error("Error Receiving " + key + " s " + packet.numFragments + " r " + packet.numFragmentsReceived);
                    throw e;
                }
            }
        } catch (InvalidClassException ne) {
            log.error("Unable to deserialize this packet");
            UnableToDeserializeError error = new UnableToDeserializeError();
            error.setCreator(creator);
            error.oid = oid;
            error.member = self;
            synchronized (packets) {
                packets.remove(key);
            }

            queue.add(new UnableToDeserializePacket(error));

        }

        return;
    }


    private void performHousekeeping() {
        Object[] keys = packets.keySet().toArray();
        FragmentedObjectPacket packet;
        long currentTime = System.currentTimeMillis();
        int count = 0;

        for (int i = 0; i < keys.length; i++) {
            packet = (FragmentedObjectPacket) packets.get(keys[i]);
            if (packet == null) // this should not happen if synchronisation of the put and remove above is not changed
                continue;

            if (currentTime - packet.timestamp > DISCARD_PACKET_PERIOD) {
                packets.remove(keys[i]);
                count++;
            }
        }
        lastHousekeeping = System.currentTimeMillis();

        if (count > 0)
            log.debug("expired " + count + " old objects from current list of " + packets.size());
    }
}
