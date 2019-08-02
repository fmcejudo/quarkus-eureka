package io.quarkus.eureka.config;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public class EurekaConfiguration {

    ClientConfig clientConfig;

    public ClientConfig getClientConfig() {
        return clientConfig;
    }
}
