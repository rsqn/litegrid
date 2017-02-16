package com.mac.litegrid.clustering.communication.serialization;

/**
 * Created with IntelliJ IDEA.
 * User: mandrewes
 * Date: 15/10/13
 *
 * To change this template use File | Settings | File Templates.
 */
public interface LiteGridSerializer {

    byte[] serialize(Object o);

    <T> T deSerialize(byte[] byff);
}
