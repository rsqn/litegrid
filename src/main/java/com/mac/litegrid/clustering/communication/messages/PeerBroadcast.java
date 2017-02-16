package com.mac.litegrid.clustering.communication.messages;

import com.mac.litegrid.clustering.communication.interfaces.MGroupMessage;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mandrewes
 * Date: 10/23/13
 *
 * To change this template use File | Settings | File Templates.
 */

public class PeerBroadcast implements MGroupMessage, Serializable {
    private static final long serialVersionUID = -3596376522512394974L;
    private Member member;
    private List<Member> members;

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "PeerBroadcast{" +
                "member=" + member +
                ", members=" + members +
                '}';
    }
}
