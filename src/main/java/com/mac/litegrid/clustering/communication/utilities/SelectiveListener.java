package com.mac.litegrid.clustering.communication.utilities;

import com.mac.litegrid.clustering.communication.interfaces.MGroupListener;
import com.mac.litegrid.clustering.communication.workers.LiteGrid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: mandrewes
 * Date: 8/10/13
 *
 * To change this template use File | Settings | File Templates.
 */
public class SelectiveListener<T> implements MGroupListener<T> {
    private static Logger log = LoggerFactory.getLogger(SelectiveListener.class);
    private Selector selector;
    private Notifiable notifiable;
    private Notifiable timeOutNotifiable;
    private LiteGrid grid;
    private long timeoutMs = -1;
    private long startTimeMs;
    private Timer timer;

    public SelectiveListener<T> withGrid(LiteGrid grid) {
        this.grid = grid;
        return this;
    }

    public SelectiveListener<T> withSelector(Selector s) {
        this.selector = s;
        return this;
    }

    public SelectiveListener<T> withClass(final Class c) {
        this.selector = new Selector() {
            @Override
            public boolean passes(Object o) {
                if (o == null) {
                    return false;
                }
                if (o.getClass().isAssignableFrom(c)) {
                    return true;
                }
                return false;
            }
        };
        return this;
    }

    public SelectiveListener<T> andNotifiable(Notifiable<T> notifiable) {
        this.notifiable = notifiable;
        return this;
    }

    public SelectiveListener<T> andTimeout(long t) {
        this.timeoutMs = t;
        return this;
    }

    public SelectiveListener<T> andTimeoutNotifiable(Notifiable<T> tn) {
        this.timeOutNotifiable = tn;
        return this;
    }

    public SelectiveListener<T> startTimer() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    onTimeout();
                } finally {
                    cleanUp();
                }
            }
        };
        startTimeMs = System.currentTimeMillis();
        timer.schedule(task, timeoutMs);
        return this;
    }

    public void cleanUp() {
        timer.cancel();
        grid.deRegisterListener(this);
    }

    public void onTimeout() {
        try {
            if (timeOutNotifiable != null) {
                timeOutNotifiable.notify(null);
            }
        } catch (Exception ignore) {
            log.warn("- ", ignore);
        }
    }

    @Override
    public void event(T o) {
        if (!selector.passes(o)) {
            log.debug("Selector not passed");
            return;
        }

        // not sure how badly I want to have the below - remove to reduce complexity for now
        /*if ( timeoutMs > 0) {
            if (startTimeMs + timeoutMs < System.currentTimeMillis()) {
                cleanUp();
                onTimeout();
                return;
            }
        }
        */

        NotifiableStatus status = notifiable.notify(o);
        if ( status == NotifiableStatus.IN_PROGRESS) {
            log.debug("TimeoutListener in progress");
        } else if ( status == NotifiableStatus.COMPLETE) {
            log.debug("TimeoutListener complete");
            cleanUp();
        } else if ( status == NotifiableStatus.FAILED) {
            log.debug("TimeoutListener failed");
            cleanUp();
        }
    }
}
