package com.mac.litegrid.clustering.communication.channels;

/**
 * Created with IntelliJ IDEA.
 * User: mandrewes
 * Date: 26/07/13
 *
 * To change this template use File | Settings | File Templates.
 */
public class HostAndPort {
    private String host;
    private int port;

    public HostAndPort(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
