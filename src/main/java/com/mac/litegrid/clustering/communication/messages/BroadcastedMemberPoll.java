package com.mac.litegrid.clustering.communication.messages;

import java.io.Serializable;

/**
 * Created by mandrewes on 8/01/16.
 *
 * This is different than a memberpoll as the broadcasting channel is a work-around for AWS not having multicast.
 */
public class BroadcastedMemberPoll extends MemberPoll implements Serializable {
    private static final long serialVersionUID = -2647692258959984292L;

    private String returnHost;
    private int returnPort;

    public String getReturnHost() {
        return returnHost;
    }

    public void setReturnHost(String returnHost) {
        this.returnHost = returnHost;
    }

    public int getReturnPort() {
        return returnPort;
    }

    public void setReturnPort(int returnPort) {
        this.returnPort = returnPort;
    }
}
