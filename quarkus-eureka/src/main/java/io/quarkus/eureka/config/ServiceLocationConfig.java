package io.quarkus.eureka.config;

import java.util.Collection;

public class ServiceLocationConfig {

    private final EurekaConfiguration eurekaConfiguration;

    public ServiceLocationConfig(final EurekaConfiguration eurekaConfiguration) {
        this.eurekaConfiguration = eurekaConfiguration;
    }

    public Collection<String> getLocations() {
        return eurekaConfiguration.serviceUrl.values();
    }
}
