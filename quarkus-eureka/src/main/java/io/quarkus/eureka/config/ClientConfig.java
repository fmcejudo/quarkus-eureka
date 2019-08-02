package io.quarkus.eureka.config;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

import java.util.Map;

@ConfigGroup
public class ClientConfig {

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

}
