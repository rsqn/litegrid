package com.mac.litegrid.clustering.communication.serialization;

import com.mac.libraries.reflection.BeanAttribute;
import com.mac.libraries.reflection.ReflectionUtil;
import com.mac.libraries.serialization.XmlSerializer;
import com.mac.utils.graphinspector.GraphInspector;
import com.mac.utils.graphinspector.GraphInspectorCallBack;
import com.mac.utils.graphinspector.GraphInspectorMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: mandrewes
 * Date: 16/10/13
 *
 * To change this template use File | Settings | File Templates.
 */
public class LiteGridCustomSerializer implements LiteGridSerializer {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public byte[] serialize(Object o) {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(bos);
        final AtomicInteger writtenArrays = new AtomicInteger(0);

        byte[] buff = XmlSerializer.toXmlBytes(o);
        try {
            bos.write(buff);
        } catch (IOException e) {
            e.printStackTrace();
        }

        new GraphInspector().withMatcher(new GraphInspectorMatcher() {
            @Override
            public boolean isInterested(Object o) {
                List<BeanAttribute> attributes = ReflectionUtil.collectAttributeMetaData(o);
                for (BeanAttribute attribute : attributes) {
                    if (attribute.hasAnnotation(ByteArray.class)) {
                        log.info("Array Found " + o.getClass() + " " + attribute.getName());
                        return true;
                    }
                }
                return false;
            }
        })
                .withCallBack(new GraphInspectorCallBack() {
                    @Override
                    public boolean onCallBack(Object o) {
                        List<BeanAttribute> attributes = ReflectionUtil.collectAttributeMetaData(o);
                        for (BeanAttribute attribute : attributes) {
                            if (attribute.hasAnnotation(ByteArray.class)) {
                                log.info("Wrote an array " + o.getClass() + " " + attribute.getName());
                                try {
                                    writtenArrays.incrementAndGet();
                                    byte[] buffer = (byte[]) attribute.executeGetter(o);
                                    dos.writeInt(1);
                                    dos.writeInt(buffer.length);
                                    dos.write(buffer);
                                } catch (IOException e) {
                                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                }
                                return true;
                            }
                        }
                        return false;
                    }
                });

        if (writtenArrays.get() > 0) {
            bos.write(0);
        }

        return bos.toByteArray();
    }

    @Override
    public <T> T deSerialize(byte[] buff) {
        ByteArrayInputStream bis = new ByteArrayInputStream(buff);
        T o = null;
        int r = bis.read();
        if (r == 0) {
            o = XmlSerializer.fromXmlBytes(buff);
        }
        return o;
    }
}
