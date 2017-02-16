package com.mac.litegrid.clustering.communication.serialization;

import com.mac.libraries.reflection.ReflectionUtil;
import com.mac.litegrid.clustering.communication.messages.ApplicationMessage;
import com.mac.litegrid.clustering.communication.messages.Member;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mandrewes
 * Date: 16/10/13
 *
 * To change this template use File | Settings | File Templates.
 */
public class XmlSerializationTest {

    @Test
    public void shouldSerializeApplicationLayerMessage() throws Exception {

        List<String> srcList = new ArrayList();
        srcList.add("1");
        srcList.add("2");

        ApplicationMessage srcMsg = new ApplicationMessage();
        srcMsg.setObject(srcList);
        srcMsg.setOriginMember(new Member());

        byte[] buff = LiteGridSerialization.getSerializer().serialize(srcMsg);

        System.out.println("size is " + buff.length);
        Assert.assertTrue(buff.length > 0);

        ApplicationMessage dstMsg = LiteGridSerialization.getSerializer().deSerialize(buff);

        Assert.assertTrue(ReflectionUtil.reflectionEquals(dstMsg,srcMsg,true));
        Assert.assertTrue(((List)dstMsg.getObject()).size() == 2);


    }
}
