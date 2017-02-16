package com.mac.litegrid.clustering.communication.serialization;

import com.mac.libraries.generic.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: mandrewes
 * Date: 26/11/13
 *
 * To change this template use File | Settings | File Templates.
 */
public class LiteGridJavaSerializer implements LiteGridSerializer {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public byte[] serialize(Object o) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os = null;
        try {
            os = new ObjectOutputStream(bos);
            os.writeObject(o);
            os.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        } finally {
            IOUtil.doClose(os);
        }
        return null;
    }

    @Override
    public <T> T deSerialize(byte[] buff) {
        ByteArrayInputStream bis = new ByteArrayInputStream(buff);
        ObjectInputStream is = null;
        try {
            is = new ObjectInputStream(bis);
            Object o = is.readObject();
            return (T)o;
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            IOUtil.doClose(is);
        }
        return null;
    }
}
