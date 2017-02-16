package com.mac.litegrid.clustering.communication.workers;

import com.mac.libraries.configuration.ConfigurationSource;

/**
 * Created by mandrewes on 1/11/15.
 */
public class LiteGridFactoryBean {
    private LiteGrid grid;

    private String name;
    private ConfigurationSource configurationSource;

    public void setName(String name) {
        this.name = name;
    }

    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }

    public void init() {
        grid = LiteGridManager.getInstance().join(name,configurationSource);
    }

    public LiteGrid getGrid() {
        return grid;
    }
}
