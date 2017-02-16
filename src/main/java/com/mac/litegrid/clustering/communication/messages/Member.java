package com.mac.litegrid.clustering.communication.messages;
import com.mac.litegrid.clustering.communication.interfaces.MGroupMessage;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: mandrewes
 * Date: 11/01/2005
 *
 * To change this template use File | Settings | File Templates.
 */
public class Member implements MGroupMessage, Serializable {
//    public enum MemberStatus {NEW,MEMBER}

    private static final long serialVersionUID = 1144188298383696032L;
    private String id;
    private String gridName;
    private long startedTs;
    private long lastHeartbeat;
//    public MemberStatus status;
    private Peer peer;

    public long getStartedTs() {
        return startedTs;
    }

    public void setStartedTs(long startedTs) {
        this.startedTs = startedTs;
    }

    public Peer getPeer() {
        return peer;
    }

    public void setPeer(Peer peer) {
        this.peer = peer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGridName() {
        return gridName;
    }

    public void setGridName(String gridName) {
        this.gridName = gridName;
    }

    public void onHeartbeat() {
        lastHeartbeat = System.currentTimeMillis();
    }

    public long getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(long lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Member member = (Member) o;

        if (!id.equals(member.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Member{" +
                "id='" + id + '\'' +
                ", gridName='" + gridName + '\'' +
                ", peer='" + peer + '\'' +

                '}';
    }

    public Member copy() {
        Member ret = new Member();
        BeanUtils.copyProperties(this,ret);
        return ret;
    }



}
