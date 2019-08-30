package io.quarkus.eureka.config;

public class DefaultInstanceInfoContext implements InstanceInfoContext {

    private final String name;
    private final int port;
    private final String vipAddress;
    private final String homePageUrl;
    private final String statusPageUrl;
    private final String healthCheckUrl;

    DefaultInstanceInfoContext(final EurekaConfiguration eurekaConfiguration) {
        this.name = eurekaConfiguration.name;
        this.port = eurekaConfiguration.port;
        this.vipAddress = eurekaConfiguration.vipAddress;
        this.homePageUrl = eurekaConfiguration.homePageUrl;
        this.statusPageUrl = eurekaConfiguration.statusPageUrl;
        this.healthCheckUrl = eurekaConfiguration.healthCheckUrl;
    }

    public static InstanceInfoContext withConfiguration(final EurekaConfiguration eurekaConfiguration) {
        return new DefaultInstanceInfoContext(eurekaConfiguration);
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }

    public String getVipAddress() {
        return vipAddress;
    }

    public String getHomePageUrl() {
        return homePageUrl;
    }

    public String getStatusPageUrl() {
        return statusPageUrl;
    }

    public String getHealthCheckUrl() {
        return healthCheckUrl;
    }
}
