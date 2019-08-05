package io.quarkus.eureka.config;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

import java.util.Map;

@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public class EurekaConfiguration {

    /**
     * port where eureka server will redirect to attend the service
     */
    @ConfigItem
    Integer port;

    /**
     * name of the service as it will turn up in Eureka
     */
    @ConfigItem
    String name;

    /**
     * Name used by load balancer to redirect to the service
     */
    @ConfigItem
    String vipAddress;

    /**
     * if AWS environment, in which region this registry service is
     */
    @ConfigItem
    String region;

    /**
     *  instances of registry services in which the application will publish itself
     */
    @ConfigItem
    Map<String, String> serviceUrl;

    /**
     * Defining zone in which client should fetch for other services
     */
    @ConfigItem(defaultValue = "true")
    boolean preferSameZone;

    /**
     * default page
     */
    @ConfigItem(defaultValue = "/")
    String homePageUrl;

    /**
     * Check the application state
     */
    @ConfigItem(defaultValue = "/info/status")
    String statusPageUrl;

    /**
     * Heartbeats which ensure the application is alive
     */
    @ConfigItem(defaultValue = "/info/health")
    String healthCheckUrl;

    public String info() {
        return new StringBuilder().append("port: ").append(port)
                .append("\nname: ").append(name).toString();

    }

    public Integer getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public String getVipAddress() {
        return vipAddress;
    }

    public String getRegion() {
        return region;
    }

    public Map<String, String> getServiceUrl() {
        return serviceUrl;
    }

    public boolean isPreferSameZone() {
        return preferSameZone;
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
