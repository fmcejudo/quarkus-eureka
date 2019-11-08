package io.quarkus.eureka.client.loadBalancer;

import java.util.Optional;

public interface LoadBalancer {
    Optional<String> getHomeUrl(String appId);
}
