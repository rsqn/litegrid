package com.mac.litegrid.clustering.communication.events;


import com.mac.litegrid.clustering.communication.interfaces.MGroupEvent;
import com.mac.litegrid.clustering.communication.messages.Member;

/**
 * Created by IntelliJ IDEA.
 * User: mandrewes
 * Date: 18/01/2005
 *
 * To change this template use File | Settings | File Templates.
 */
public class MemberLeaveEvent implements MGroupEvent {
    private Member member;

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}
