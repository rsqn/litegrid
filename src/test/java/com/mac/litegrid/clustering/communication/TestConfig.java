package com.mac.litegrid.clustering.communication;

import com.mac.libraries.configuration.ConfigurationSource;
import com.mac.libraries.configuration.MapConfigurationSource;
import com.mac.libraries.configuration.PropertiesFileConfigurationSource;
import com.mac.platform.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * Created with IntelliJ IDEA.
 * User: mandrewes
 * Date: 10/25/13
 *
 * To change this template use File | Settings | File Templates.
 */
public class TestConfig {
    protected static  Logger log = LoggerFactory.getLogger(TestConfig.class);

    public static ConfigurationSource getConfig() {
        String propsResource = null;
        ConfigurationSource configSource = null;
        if (!StringUtil.isEmpty(System.getProperty("litegrid.properties"))) {
            propsResource = System.getProperty("litegrid.properties");
        }
        log.info("litegrid.properties=" + propsResource);
        if ( propsResource == null) {
            return new MapConfigurationSource();
        }

        ClassPathResource resource = new ClassPathResource(propsResource);
        if (resource.exists()) {
            log.info("Picked up properties");
            PropertiesFileConfigurationSource propsConfig = new PropertiesFileConfigurationSource(propsResource);
            configSource = propsConfig;
        } else {
            log.info("No Properties file found");
            configSource = new MapConfigurationSource();
        }
        return configSource;
    }
}
