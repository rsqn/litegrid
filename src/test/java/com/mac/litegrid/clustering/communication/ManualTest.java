package com.mac.litegrid.clustering.communication;

import com.mac.libraries.configuration.ConfigurationSource;
import com.mac.libraries.configuration.PropertiesFileConfigurationSource;
import com.mac.libraries.generic.RandomUtil;
import com.mac.libraries.parsing.ArgumentParser;
import com.mac.litegrid.clustering.communication.interfaces.MGroupListener;
import com.mac.litegrid.clustering.communication.utilities.LiteMap;
import com.mac.litegrid.clustering.communication.workers.LiteGrid;
import com.mac.litegrid.clustering.communication.workers.LiteGridManager;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.concurrent.BlockingQueue;

/**
 * Created by IntelliJ IDEA.
 * User: mandrewes
 * Date: 11/01/2005
 *
 * To change this template use File | Settings | File Templates.
 */
public class ManualTest extends Thread implements MGroupListener {
    BlockingQueue q;

    static int ctr = 0;
    public static LiteGrid mgroup;
    static boolean closewhen = true;

    private LiteGrid g;

    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack","true");

        try {

            ConfigurationSource configSrc = new PropertiesFileConfigurationSource(ArgumentParser.getSingleArgument("config",args));

//            LiteMap hashtable = new LiteMap(LiteMap.MODE_VERY_OPTIMISTIC, "xmds",TestConfig.getConfig());


            LiteGrid grid = LiteGridManager.getInstance().join("test",configSrc);

            grid.registerListener((e) -> {
                System.out.println(ToStringBuilder.reflectionToString(e));
            });

            ManualTest manualTest = new ManualTest();

            manualTest.g = grid;

            manualTest.start();

            Thread.sleep(1000*60*10);

        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        String f = RandomUtil.getRandomAlpha(3);

        for (int i = 0; i < 20; i++) {
            try {
                Thread.sleep(10000);
                g.sendAsync("from " + f);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void event(Object o) {
        ctr++;

        System.out.println(o);
        try {
            if (ctr == 50 && closewhen)
                LiteGridManager.getInstance().deRegisterListener(this, "test thing");
        } catch (Exception e) {
        }

    }


}
