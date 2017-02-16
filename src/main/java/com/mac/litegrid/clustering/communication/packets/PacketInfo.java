package com.mac.litegrid.clustering.communication.packets;

/**
 * Created by IntelliJ IDEA.
 * User: mandrewes
 * Date: 20/01/2005
 *
 * To change this template use File | Settings | File Templates.
 */
public class PacketInfo {
    public String creator;
    public long oid;
    public int bytesOut;

    public PacketInfo() {
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public long getOid() {
        return oid;
    }

    public void setOid(long oid) {
        this.oid = oid;
    }

    public int getBytesOut() {
        return bytesOut;
    }

    public void setBytesOut(int bytesOut) {
        this.bytesOut = bytesOut;
    }
}
