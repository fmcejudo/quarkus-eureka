package io.quarkus.eureka.config;

public class Client {

    private EurekaConfiguration eurekaConfiguration;

    public Client(EurekaConfiguration eurekaConfiguration) {
        this.eurekaConfiguration = eurekaConfiguration;
    }

    public Integer getPort() {
        return eurekaConfiguration.port;
    }

    public String getName() {
        return eurekaConfiguration.name;
    }

}
