package com.mac.litegrid.clustering.communication;

import com.mac.litegrid.clustering.communication.utilities.LiteMap;
import com.mac.litegrid.clustering.communication.utilities.LiteMapListener;

/**
 * Created with IntelliJ IDEA.
 * User: mandrewes
 * Date: 19/09/13
 *
 * To change this template use File | Settings | File Templates.
 */
public class LiteMapTest {

    public static void main(String[] args) {
        try {

            LiteMapTest test = new LiteMapTest();
            if ( "send".equals(args[0])) {
                test.send();
            } else {
                test.receive();
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }


    public void send() throws Exception {
        LiteMap<String> map = new LiteMap(LiteMap.MODE_VERY_OPTIMISTIC, "testmap",TestConfig.getConfig());

        for (int i = 0; i < 20; i++) {
            System.out.println("Sending");
            map.put("k" + i, "v" + i);
            System.out.println("send size " + map.size());
            Thread.sleep(5000);
        }
    }

    public void receive() throws Exception {
        LiteMap<String> map = new LiteMap(LiteMap.MODE_VERY_OPTIMISTIC, "testmap",TestConfig.getConfig());

        map.addListener(new LiteMapListener<String, String>() {
            @Override
            public void onUpdate(String k, String v) {
                System.out.println("onUpdate " + k + " = " + v );
            }

            @Override
            public void onRemove(String k) {
                System.out.println("onRemove " + k);
            }
        });
        while(true) {
            System.out.println("map size " + map.size());
            Thread.sleep(5000);
        }
    }
}
