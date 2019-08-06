package io.quarkus.eureka;

import io.quarkus.eureka.client.InstanceInfo;
import io.quarkus.eureka.config.EurekaConfiguration;
import io.quarkus.eureka.config.InstanceInfoBuilder;
import io.quarkus.eureka.config.ServiceLocationConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class EurekaProducer {

    private EurekaConfiguration eurekaConfiguration;

    @Produces
    @Dependent
    public InstanceInfo instanceInfo() {
        return InstanceInfoBuilder.fromConfig(eurekaConfiguration).build();
    }

    @Produces
    @Dependent
    public ServiceLocationConfig serviceLocationConfig() {
        return new ServiceLocationConfig(eurekaConfiguration);
    }

    void setConfiguration(EurekaConfiguration eurekaConfiguration) {
        this.eurekaConfiguration = eurekaConfiguration;
    }
}
