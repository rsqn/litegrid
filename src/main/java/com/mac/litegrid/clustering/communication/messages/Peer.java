package com.mac.litegrid.clustering.communication.messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mandrewes
 * Date: 10/23/13
 *
 * To change this template use File | Settings | File Templates.
 */

//todo: - have member extend peer, rather than the other way around
public class Peer implements Serializable {
    private static final long serialVersionUID = -7773910904324800160L;
    private String memberId;
    public String address;
    public int discoveryPort;
    public int tcpMemberChannelPort;
    public int tcpCommsChannelPort;
    public long localLastReceived;

    public int getDiscoveryPort() {
        return discoveryPort;
    }

    public void setDiscoveryPort(int discoveryPort) {
        this.discoveryPort = discoveryPort;
    }

    transient long lastConnectionAttempt;
    transient long lastConnected;
    transient long retryIntervalMs;
    transient List<Integer> retryExponent = new ArrayList<>();
    transient long currentRetryCount = 0;

//    public boolean doesHostAndPortMatch(Peer p) {
//        return p.getAddress().equals(address) && (p.getTcpMemberChannelPort() == tcpMemberChannelPort);
//    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    @Override
    public int hashCode() {
        return getMemberId().hashCode();
    }

//    public String getId() {
//        return address + ":" + tcpMemberChannelPort + ":" + memberId;
//    }

    public long getLastConnectionAttempt() {
        return lastConnectionAttempt;
    }

    public void setLastConnectionAttempt(long lastConnectionAttempt) {
        this.lastConnectionAttempt = lastConnectionAttempt;
    }

    public long getLastConnected() {
        return lastConnected;
    }

    public void setLastConnected(long lastConnected) {
        this.lastConnected = lastConnected;
    }

    public long getRetryIntervalMs() {
        return retryIntervalMs;
    }

    public void setRetryIntervalMs(long retryIntervalMs) {
        this.retryIntervalMs = retryIntervalMs;
    }

    public List<Integer> getRetryExponent() {
        return retryExponent;
    }

    public void setRetryExponent(List<Integer> retryExponent) {
        this.retryExponent = retryExponent;
    }

    public long getCurrentRetryCount() {
        return currentRetryCount;
    }

    public void setCurrentRetryCount(long currentRetryCount) {
        this.currentRetryCount = currentRetryCount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getTcpMemberChannelPort() {
        return tcpMemberChannelPort;
    }

    public void setTcpMemberChannelPort(int tcpMemberChannelPort) {
        this.tcpMemberChannelPort = tcpMemberChannelPort;
    }

    public int getTcpCommsChannelPort() {
        return tcpCommsChannelPort;
    }

    public void setTcpCommsChannelPort(int tcpCommsChannelPort) {
        this.tcpCommsChannelPort = tcpCommsChannelPort;
    }

    public long getLocalLastReceived() {
        return localLastReceived;
    }

    public void setLocalLastReceived(long localLastReceived) {
        this.localLastReceived = localLastReceived;
    }

    public void onFailedConnection() {
        currentRetryCount++;
    }

    public void onSuccessfullConnection() {
        currentRetryCount = 0;
        lastConnectionAttempt = System.currentTimeMillis();
        lastConnected = System.currentTimeMillis();
    }

//    public void onHeartBeat() {
//        currentRetryCount = 0;
//        lastConnected = System.currentTimeMillis();
//    }


    public long calculateNextRetry() {
        return lastConnectionAttempt + retryIntervalMs;
    }


    @Override
    public String toString() {
        return "Peer{" +
                "memberId='" + memberId + '\'' +
                ", address='" + address + '\'' +
                ", tcpMemberChannelPort=" + tcpMemberChannelPort +
                ", tcpCommsChannelPort=" + tcpCommsChannelPort +
                ", discoveryPort=" + discoveryPort +

                '}';
    }
}
