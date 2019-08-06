package io.quarkus.eureka.config;

import io.quarkus.eureka.client.InstanceInfo;

public final class InstanceInfoBuilder {

    private final EurekaConfiguration eurekaConfiguration;

    private InstanceInfoBuilder(final EurekaConfiguration eurekaConfiguration) {
        this.eurekaConfiguration = eurekaConfiguration;
    }

    public static InstanceInfoBuilder fromConfig(final EurekaConfiguration eurekaConfiguration) {
        return new InstanceInfoBuilder(eurekaConfiguration);
    }

    public InstanceInfo build() {
        return InstanceInfo.of(new InstanceInfoContext(eurekaConfiguration));
    }
}
