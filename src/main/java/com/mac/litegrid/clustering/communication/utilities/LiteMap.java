package com.mac.litegrid.clustering.communication.utilities;


import com.mac.libraries.concurrency.Notifier;
import com.mac.libraries.configuration.ConfigurationSource;
import com.mac.litegrid.clustering.communication.events.MemberJoinEvent;
import com.mac.litegrid.clustering.communication.interfaces.MGroupListener;
import com.mac.litegrid.clustering.communication.messages.distributedhashtable.DistributedHashUpdate;
import com.mac.litegrid.clustering.communication.workers.LiteGrid;
import com.mac.litegrid.clustering.communication.workers.LiteGridManager;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: mandrewes
 * Date: 12/01/2005
 *
 * To change this template use File | Settings | File Templates.
 */


public class LiteMap<V> implements MGroupListener { // wondering if I should extend or not..
    protected Logger log = LoggerFactory.getLogger(getClass());
    public static final int MODE_VERY_OPTIMISTIC = 1;
    private volatile long sequenceNo = 0;
    private Hashtable<String, V> localCopy;
    private int mode;
    private LiteGrid mgroup;
    private String name;
    private Notifier notifier;


    public LiteGrid getMGroup() {
        return mgroup;
    }

    public LiteMap(int mode, String name, ConfigurationSource config) {
        this.mode = mode;
        this.name = name;
        this.mgroup = LiteGridManager.getInstance().registerListener(this, name, config); // have ability to want to know member joins
        localCopy = new Hashtable();
        notifier = new Notifier();

        if (mode == MODE_VERY_OPTIMISTIC) {
            return;
        }

        log.info("LiteMap Initialized name[" + name + "]");
    }


    public Set<String> keySet() {
        return ((Hashtable) localCopy.clone()).keySet();
    }

    public Collection<V> values() {
        return ((Hashtable) localCopy.clone()).values();
    }

    public void event(Object o) {
        DistributedHashUpdate update;
        try {
            if (mode == MODE_VERY_OPTIMISTIC) {
                veryOptimisticEvent(o);
                if(o instanceof MemberJoinEvent){
                    putLocalCopy();
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void putLocalCopy(){
        Iterator it = localCopy.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            put((String)pair.getKey(),(V)pair.getValue());
        }
    }

    public int size() {
        return localCopy.size();
    }

    public void stop() {
        notifier.shutdown();
        LiteGridManager.getInstance().deRegisterListener(this, name);
    }

    public void addListener(LiteMapListener<String, V> listener) {
        notifier.addListener(listener);
    }

    private void veryOptimisticEvent(Object o) throws Exception {
        DistributedHashUpdate<V> update;

        log.debug("LiteMap received Very Optimistic Update " + ToStringBuilder.reflectionToString(o));
        /* Receive and handle an update ****************************************************************/
        if (o instanceof DistributedHashUpdate) {
            update = (DistributedHashUpdate) o;
            log.debug("received " + update.key + " : " + update.getValue());
            if (update.remove) {
                localCopy.remove(update.key);
                notifier.notifyListeners("onRemove", update.key);
            } else {
                V value = (V)update.getValue();

                localCopy.put(update.key, value);
                notifier.notifyListeners("onUpdate", update.key, value);
            }
            /* Receive and handle a sync request ****************************************************************/
        }
    }


    public void put(String key, V value) {
        try {
            if (mode == MODE_VERY_OPTIMISTIC) {
                updateVeryOptimistic(key, value, false);
            }
        } catch (Exception e) {
            log.info("LiteMap Exception sending update of " + key + " " + e.getMessage());
        }
    }


    public boolean containsKey(String key) {
        return localCopy.containsKey(key);
    }

    public boolean containsValue(V value) {
        return localCopy.containsValue(value);
    }

    public Set<String> copyKeys() {
        Set<String> ret = new HashSet();
        ret.addAll(localCopy.keySet());
        return ret;
    }

    public void remove(String key) {
        try {
            updateVeryOptimistic(key, null, true);
        } catch (Exception e) {
            throw new RuntimeException("LiteMap Exception during remove " + e.getMessage(), e);
        }
    }

    private void updateVeryOptimistic(String key, V value, boolean remove) {
        DistributedHashUpdate update = new DistributedHashUpdate();
        update.timestamp = System.currentTimeMillis();
        update.key = key;
        update.value = value;
        update.member = mgroup.getSelf();
        update.remove = remove;
        update.setSequenceid(sequenceNo++);

        if (remove) {
            localCopy.remove(key);
        } else {
            localCopy.put(key, value);
        }
        mgroup.sendAsync(update);
        return;
    }


    public V get(String key) {
        return localCopy.get(key);
    }


}
