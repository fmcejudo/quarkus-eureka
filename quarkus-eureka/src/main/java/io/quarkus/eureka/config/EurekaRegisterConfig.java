package io.quarkus.eureka.config;

import io.quarkus.eureka.util.HostNameDiscovery;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EurekaRegisterConfig {

    private final EurekaConfiguration eurekaConfiguration;

    private EurekaRegisterConfig(EurekaConfiguration eurekaConfiguration) {
        this.eurekaConfiguration = eurekaConfiguration;
    }

    static EurekaRegisterConfig from(final EurekaConfiguration eurekaConfiguration) {
        return new EurekaRegisterConfig(eurekaConfiguration);
    }

    Map<String, Object> getMap() {

        final String hostname = HostNameDiscovery.getHostname();

        Map<String, Object> instance;

        {
            instance = new HashMap<String, Object>() {{
                put("hostName", hostname);
                put("app", eurekaConfiguration.name.toUpperCase());
                put("vipAddress", eurekaConfiguration.vipAddress);
                put("secureVipAddress", eurekaConfiguration.vipAddress);
                put("ipAddr", hostname);
                put("status", "UP");
                put("port", new HashMap<String, String>() {{
                    put("$", String.valueOf(eurekaConfiguration.port));
                    put("@enabled", "true");
                }});
                put("securePort", new HashMap<String, String>() {{
                    put("$", String.valueOf(eurekaConfiguration.port));
                    put("@enabled", "false");
                }});
                put("healthCheckUrl", String.format("http://%s:%d/%s", hostname, eurekaConfiguration.port, eurekaConfiguration.healthCheckUrl));
                put("statusPageUrl", String.format("http://%s:%d/%s", hostname, eurekaConfiguration.port, eurekaConfiguration.statusPageUrl));
                put("homePageUrl", String.format("http://%s:%d/%s", hostname, eurekaConfiguration.port, eurekaConfiguration.homePageUrl));
                put("dataCenterInfo", new HashMap<String, Object>() {{
                    put("@class", "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo");
                    put("name", "MyOwn");
                }});
            }};
        }

        return Collections.singletonMap("instance", instance);
    }

}
