package com.mac.litegrid.clustering.communication.workers;

import com.mac.libraries.configuration.ConfigurationSource;
import com.mac.libraries.deployment.ModuleVersion;
import com.mac.litegrid.clustering.communication.interfaces.MGroupListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: mandrewes
 * Date: 11/01/2005
 *
 * To change this template use File | Settings | File Templates.
 */
public class LiteGridManager {
    private static LiteGridManager jvmManager;
    private static final String DEFFAULT_ADDRESS = "230.0.0.1";

    static {
        ModuleVersion.setVersion("litegrid", "1.3.0");
    }

    public Map groups;

    public static int getUniquePort(String name) {
        return (name.hashCode() % 101) + 52000;
    }

    public static synchronized LiteGridManager getInstance() {
        if (jvmManager == null)
            jvmManager = new LiteGridManager();
        return jvmManager;
    }

    public LiteGridManager() {
        groups = new HashMap();
    }

    public LiteGrid registerListener(MGroupListener listener, String name, ConfigurationSource config) {
        try {
            synchronized (groups) {
                LiteGrid mgroup = (LiteGrid) groups.get(name);
                if (mgroup == null) {
                    mgroup = new LiteGrid(DEFFAULT_ADDRESS, name, config);
                    groups.put(name, mgroup);
                }
                mgroup.registerListener(listener);
                return mgroup;
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception registering listener " + name + " -" + e, e);
        }

    }


    public void deRegisterListener(MGroupListener listener, String name) {
        synchronized (groups) {
            LiteGrid mgroup = (LiteGrid) groups.get(name);
            if (mgroup == null) {
                return;
            }
            mgroup.deRegisterListener(listener);
        }
    }

    public void leave(String name) {
        try {
            synchronized (groups) {
                LiteGrid mgroup = (LiteGrid) groups.get(name);

                if (mgroup == null) {
                   return;
                }
                groups.remove(name);
                mgroup.leave();
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception joining mgroup " + name + " - " + e, e);
        }
    }

    public LiteGrid join(String name, ConfigurationSource config) {
        try {
            synchronized (groups) {

                LiteGrid mgroup = (LiteGrid) groups.get(name);

                if (mgroup == null) {
                    mgroup = new LiteGrid(DEFFAULT_ADDRESS, name, config);
                    groups.put(name, mgroup);
                }
                return mgroup;

            }
        } catch (Exception e) {
            throw new RuntimeException("Exception joining mgroup " + name + " - " + e, e);
        }
    }


}
