package com.mac.litegrid.clustering.communication.messages;

import com.mac.litegrid.clustering.communication.interfaces.MGroupMessage;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: mandrewes
 * Date: 11/01/2005
 * <p>
 * To change this template use File | Settings | File Templates.
 */
public class MemberPoll implements MGroupMessage, Serializable {
    private static final long serialVersionUID = -2647694258959984292L;
    private String from;

    private boolean requestPeerBroadcast;

    public MemberPoll() {
    }

    public MemberPoll(String from) {
        this.from = from;
    }

    public MemberPoll(String from, boolean requestPeerBroadcast) {
        this.from = from;
        this.requestPeerBroadcast = requestPeerBroadcast;
    }

    public boolean isRequestPeerBroadcast() {
        return requestPeerBroadcast;
    }

    public void setRequestPeerBroadcast(boolean requestPeerBroadcast) {
        this.requestPeerBroadcast = requestPeerBroadcast;
    }

    public String toString() {
        return "Poll from " + from + " requestPeerBroadcast = " + requestPeerBroadcast;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
