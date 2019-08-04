package io.quarkus.eureka.config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class EurekaProducer {

    private EurekaConfiguration eurekaConfiguration;

    @Produces
    @Dependent
    public Client client() {
        return new Client(eurekaConfiguration);
    }


    public void setConfiguration(EurekaConfiguration eurekaConfiguration) {
        this.eurekaConfiguration = eurekaConfiguration;
    }
}
