package com.mac.litegrid.clustering.communication.serialization;

import com.mac.libraries.serialization.XmlSerializer;

/**
 * Created with IntelliJ IDEA.
 * User: mandrewes
 * Date: 16/10/13
 *
 * To change this template use File | Settings | File Templates.
 */
public class LiteGridXmlSerializer implements LiteGridSerializer {

    @Override
    public byte[] serialize(Object o) {
        byte[] buff =XmlSerializer.toXmlBytes(o);
        return buff;
    }

    @Override
    public <T> T deSerialize(byte[] buff) {
        T o = XmlSerializer.fromXmlBytes(buff);
        return o;
    }
}
