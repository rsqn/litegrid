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
public class ReceiveConfirmation implements ObjectReceivedResponse, Serializable {
    private static final long serialVersionUID = 4733349752615565501L;
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

    public Member getMember() {
        return member;
    }

    public ReceiveConfirmation() {
    }

    public String toString() {
        //return member.id + ":" + member.address + "Unable to Deserialize Object " + oid;
        return "";
    }



    public long getOid() {
        return oid;
    }

    public void setOid(long oid) {
        this.oid = oid;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}
