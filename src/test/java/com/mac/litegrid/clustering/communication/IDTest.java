package com.mac.litegrid.clustering.communication;

import com.mac.litegrid.clustering.communication.interfaces.MGroupListener;
import com.mac.litegrid.clustering.communication.messages.Member;
import com.mac.litegrid.clustering.communication.workers.LiteGrid;
import com.mac.litegrid.clustering.communication.workers.LiteGridManager;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mandrewes
 * Date: 29/11/2005
 *
 * To change this template use File | Settings | File Templates.
 */
public class IDTest extends Thread {

    public IDTest() {

        start();
    }

    public void run() {
        System.out.println("Starting..");
        try {
            LiteGrid group = LiteGridManager.getInstance().join("beatnick", TestConfig.getConfig());


            group.registerListener(new MGroupListener() {
                @Override
                public void event(Object o) {
                    System.out.println("XXXXX got an event " + o);
                }
            });

            for (int i = 0; i < 1000; i++) {
                String s = "OUT MEMBERS" + " [";
                List<Member> keys = group.getSortedMembers(false);
                //Member member;

                s += "SELF (" + group.getSelf() + ")\nOthers:\n";

                for (int j = 0; j < keys.size(); j++) {
                    s += keys.get(j) + "\n";
                }
                s += "\n";
                System.out.println(s);

                if ( keys.size() > 0) {
                    SomeObject so = new SomeObject();
                    so.setName("name" + i + " from " + group.getSelf().getId());
                    for (int j = 0; j < 20; j++) {
                        so.addValue("value " + i + ".j");
                    }
                    so.addValue("egh");
                    System.out.println("Sending " + so);
                    group.sendAsync(so);
                }
                sleep(3000);
            }

            System.out.println("run done");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

        try {

            System.setProperty("litegrid.properties","a_litegrid.properties");
            IDTest test = new IDTest();

            System.out.println("finished creating threads");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
