package com.mac.litegrid.clustering.communication.utilities;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: mandrewes
 * Date: 12/12/2005
 *
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class ObjectUtils {

    public static long getDeserializedSize(Object o) throws NotSerializableException, IOException {
        ByteArrayOutputStream bstream = new ByteArrayOutputStream();
        ObjectOutputStream ostream = new ObjectOutputStream(bstream);
        ostream.writeObject(o);
        return bstream.toByteArray().length;
    }

    public static byte[] serializeObejct(Object o) throws NotSerializableException, IOException {
        ByteArrayOutputStream bstream = new ByteArrayOutputStream();
        ObjectOutputStream ostream = new ObjectOutputStream(bstream);
        ostream.writeObject(o);
        return bstream.toByteArray();
    }

    public static Object deSerialize(byte[] b) throws IOException {
        Object o;
        ByteArrayInputStream bstream = new ByteArrayInputStream(b);
        ObjectInputStream istream = new ObjectInputStream(bstream);
        try {
            o = istream.readObject();
        } catch (Exception e) {
            throw new InvalidClassException(e.getMessage()); // keep it as just one exception
        }
        istream.close();
        return o;
    }
}
