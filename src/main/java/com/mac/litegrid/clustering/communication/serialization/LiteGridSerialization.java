package com.mac.litegrid.clustering.communication.serialization;

/**
 * Created with IntelliJ IDEA.
 * User: mandrewes
 * Date: 16/10/13
 *
 * To change this template use File | Settings | File Templates.
 */
public class LiteGridSerialization {

    static LiteGridSerializer instance = new LiteGridJavaSerializer();

    public static LiteGridSerializer getSerializer() {
        return instance;
    }
}
