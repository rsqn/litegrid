package com.mac.litegrid.clustering.communication.workers;

/**
 * Created by IntelliJ IDEA.
 * User: mandrewes
 * Date: 12/12/2005
 *
 * To change this template use File | Settings | File Templates.
 */
public class FlowController {
    private int rate;
    private int count;
    private long lastSample;
    private static final long timeperiod = 1 * 1000;
    private long throttle;

    public FlowController(int rate) {
        this.rate = rate;
        count = 0;
        lastSample = System.currentTimeMillis();
        if (rate > 0) {// should ALWAYS be
            throttle = timeperiod / rate;
        } else {
            rate = 100;
        }
    }

    public synchronized void controlMyFlow() {
        long currentSample = System.currentTimeMillis();
        long difference = currentSample - lastSample;

        if (difference <= timeperiod) {
            count++;
            if (count >= rate) {
                try {
                    wait(throttle);
                } catch (InterruptedException ignore) {

                }
            }
        } else {
            lastSample = currentSample;
            count = 0;
        }
        count++;

    }

}
