package com.mac.litegrid.clustering.communication.packets;

import com.mac.libraries.generic.RandomUtil;
import com.mac.litegrid.clustering.communication.interfaces.NetworkMessage;
import com.mac.litegrid.clustering.communication.serialization.LiteGridSerialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: mandrewes
 * Date: 10/01/2005
 *
 * To change this template use File | Settings | File Templates.
 * <p/>
 * <p/>
 * mm frags...
 */
public class FragmentedObjectPacket implements NetworkMessage {
    protected static Logger log = LoggerFactory.getLogger(FragmentedObjectPacket.class);
    public String creator = "";
    public long oid = -1;
    public int security = -1; // used to assist with separating multicast traffic packets when multiple otherMembers join using ID 1

    private transient byte[] fragmentMap; // testing and debugging
    public int numFragments;
    public transient int numFragmentsReceived;
    public static int MAX_SIZE = 512; // TODO make larger ( 256/512 - final should be about 2-5k ) this CAN send large messages, but preferrably only small ones, larger messages should be targeted through the TCP channel to individual otherMembers

    private transient Object o;
    private transient boolean confirmReceive = false;
    public transient long timestamp; // this particular object should never go across the network however

    transient byte[] g_buff;
    transient int writeOffset = 0;
    transient int fragmentCtr = 0;

    public static final void setMaxFragmentSizeBytes(int sz) {
        FragmentedObjectPacket.MAX_SIZE = sz;
    }

    public FragmentedObjectPacket() {
    }

    public Object getObject() {
        return o;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public long getObjectId() {
        return oid;
    }

    public void setConfirmationRequired(boolean confirm) {
        confirmReceive = confirm;
    }

    public boolean getConfirmationRequired() {
        return confirmReceive;
    }

    public String describeMissingFragments() {
        String ret = "";
        if (numFragments != numFragmentsReceived) {
            ret += "Missing Frags " + creator + ":" + security + ":" + oid + ">";
        }

        for (int i = 0; i < fragmentMap.length; i++) {
            if (fragmentMap[i] != 1) {
                ret += i + ",";
            }
        }
        return ret;
    }

    public void setObject(String creator, long objectId, Object o) {
        oid = objectId;
        this.creator = creator;
        security = RandomUtil.getRange(1000, 1000000);
        g_buff = serialize(o);
        numFragments = (int) Math.ceil((double) g_buff.length / (double) MAX_SIZE);
        fragmentCtr = 0;
        //numFragmentsReceived = 0;

        writeOffset = 0;
    }

//    public static long[] getCreatorAndOIDAndSecurity(byte[] buff) throws Exception {
//        ByteArrayInputStream bstream = new ByteArrayInputStream(buff);
//        DataInputStream istream = new DataInputStream(bstream);
//        String creator = istream.readUTF();
//        long oid = istream.readLong();
//        int security = istream.readInt();
//        //int numfrags = istream.readInt();
//        istream.close();
//        //return new int[] {creator,oid,numfrags};
//        return new long[]{creator, oid, security};
//    }

    public synchronized boolean receiveFragment(byte[] buff) throws Exception {
        ByteArrayInputStream bstream = new ByteArrayInputStream(buff);
        DataInputStream istream = new DataInputStream(bstream);
        String creator = istream.readUTF();
        long oid = istream.readLong();
        int security = istream.readInt();
        int numFragments = istream.readInt();
        int totalSize = istream.readInt();
        int fragmentNumber = istream.readInt();
        if (fragmentNumber == 0)
            confirmReceive = istream.readBoolean();
        int datalen = istream.readInt();
        byte[] data = new byte[datalen];
        istream.read(data);
        istream.close();

        if (this.numFragments == 0) {
            fragmentMap = new byte[numFragments];
            //if (numFragments > 5)
            //  log.debug("NEW PAK c" + creator + " o" + oid + " n" + numFragments + " s" + fragmentNumber + " d" + datalen);
        }

        if (fragmentMap[fragmentNumber] == 1) {
            log.error("DUPLICATE FRAGMENT!!" + creator + ":" + oid + ":" + security); // this is supposed to be possible (retransmission), however would usually indicate that two systems have erronously used the same creator/oid/security combination
            // the security random is there to prevent this (more info at it's declaration)
            return false;
        }

        fragmentMap[fragmentNumber] = 1;


        //log.debug("Receiving fragment for " + oid + " frag " + fragmentNumber + " of " + numFragments + " datalen " + datalen);

        if (this.oid == -1) {
            this.creator = creator;
            this.oid = oid;
            this.security = security;
            this.numFragments = numFragments;
            g_buff = new byte[totalSize];
        }

        numFragmentsReceived++;

        for (int i = 0; i < data.length; i++) {
            g_buff[(fragmentNumber * MAX_SIZE) + i] = data[i];
        }

        if (numFragmentsReceived == numFragments) {
            deSerialize();
            //log.debug("Received All Fragments");
            return true;
        }

        return false;
    }

    public byte[] getNextFragment() {
        try {
            int len = 0;
            if (writeOffset >= g_buff.length)
                return new byte[0];

            ByteArrayOutputStream bstream = new ByteArrayOutputStream();
            DataOutputStream ostream = new DataOutputStream(bstream);
            len = FragmentedObjectPacket.MAX_SIZE;
            if (len + writeOffset > g_buff.length)
                len = g_buff.length - writeOffset;

            ostream.writeUTF(creator);
            ostream.writeLong(oid);
            ostream.writeInt(security);
            ostream.writeInt(numFragments);
            ostream.writeInt(g_buff.length);
            ostream.writeInt(fragmentCtr);
            if (fragmentCtr == 0)
                ostream.writeBoolean(confirmReceive);

            ostream.writeInt(len);
            ostream.write(g_buff, writeOffset, len);


            writeOffset += MAX_SIZE;
            fragmentCtr++;

            return bstream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Exception in getNextFragment " + e, e);
        }
    }

    private byte[] serialize(Object o) {
        try {
            byte[] buff = LiteGridSerialization.getSerializer().serialize(o);
            return buff;
        } catch (Exception e) {
            throw new RuntimeException("Error serializing packet " + e, e);
        }
    }

    private void deSerialize() throws Exception {
        o = LiteGridSerialization.getSerializer().deSerialize(g_buff);
    }
}
