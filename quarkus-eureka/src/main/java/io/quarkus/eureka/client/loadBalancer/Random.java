package io.quarkus.eureka.client.loadBalancer;

import io.quarkus.eureka.util.ServiceDiscovery;

import java.util.Optional;

public class Random implements LoadBalancer {
    private final ServiceDiscovery serviceDiscovery;

    public Random(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    @Override
    public Optional<String> getHomeUrl(String appId) {
        return serviceDiscovery.findServiceLocations(appId).findAny();
    }
}
