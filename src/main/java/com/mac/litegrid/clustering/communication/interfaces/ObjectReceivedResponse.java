package com.mac.litegrid.clustering.communication.interfaces;

import com.mac.litegrid.clustering.communication.messages.Member;

/**
 * Created by IntelliJ IDEA.
 * User: mandrewes
 * Date: 2/12/2005
 *
 * To change this template use File | Settings | File Templates.
 */
public interface ObjectReceivedResponse {
    public String getCreator();
    public long getOID();
    public Member getMember();
}
