package com.mac.litegrid.clustering.communication.messages;

import com.mac.litegrid.clustering.communication.interfaces.ObjectReceivedResponse;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: mandrewes
 * Date: 12/01/2005
 *
 * To change this template use File | Settings | File Templates.
 * <p/>
 * Used for SYNC messages, not a failure/retry mechanism, as this is ALSO a fragmented packet (takes 3)
 */
public class UnableToDeserializeError implements Serializable, ObjectReceivedResponse {
    private static final long serialVersionUID = -2934034054814559704L;
    //public boolean receivedOk;
    public String creator;
    public long oid;
    //public int security;
    public Member member;


    public long getOID() {
        return oid;
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

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}
