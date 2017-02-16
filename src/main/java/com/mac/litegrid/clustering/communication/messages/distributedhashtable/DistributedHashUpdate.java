package com.mac.litegrid.clustering.communication.messages.distributedhashtable;

import com.mac.litegrid.clustering.communication.messages.Member;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: mandrewes
 * Date: 12/01/2005
 *
 * To change this template use File | Settings | File Templates.
 */
public class DistributedHashUpdate<V> implements Serializable {
    private static final long serialVersionUID = 949194242934649469L;

    public long timestamp;
    public Member member;
    public String key;
    public Object value;
    public boolean remove;
    public long sequenceid;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isRemove() {
        return remove;
    }

    public void setRemove(boolean remove) {
        this.remove = remove;
    }

    public long getSequenceid() {
        return sequenceid;
    }

    public void setSequenceid(long sequenceid) {
        this.sequenceid = sequenceid;
    }

}
