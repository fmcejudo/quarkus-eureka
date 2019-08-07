package io.quarkus.eureka.config;

public interface InstanceInfoContext {

    String getName();
    int getPort();
    String getVipAddress();
    String getHomePageUrl();
    String getStatusPageUrl();
    String getHealthCheckUrl();
}
