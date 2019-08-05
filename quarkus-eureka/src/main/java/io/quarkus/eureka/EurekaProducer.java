package io.quarkus.eureka;

import io.quarkus.eureka.config.EurekaConfiguration;
import io.quarkus.eureka.config.UrlEurekaClient;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class EurekaProducer {

    private EurekaConfiguration eurekaConfiguration;

    @Produces
    @Dependent
    public UrlEurekaClient client() {
        return new UrlEurekaClient(eurekaConfiguration);
    }

    void setConfiguration(EurekaConfiguration eurekaConfiguration) {
        this.eurekaConfiguration = eurekaConfiguration;
    }
}
