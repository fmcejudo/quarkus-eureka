package io.quarkus.eureka.client;

import io.quarkus.eureka.client.loadBalancer.LoadBalancer;
import io.quarkus.eureka.exception.EurekaServiceNotFoundException;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.ws.rs.client.WebTarget;
import java.util.function.Supplier;

public class EurekaClient {

    private final LoadBalancer loadBalancer;
    private final ResteasyClient client;

    public EurekaClient(final LoadBalancer loadBalancer) {
        this.client = ((ResteasyClientBuilder) ResteasyClientBuilder.newBuilder()).connectionPoolSize(50).build();
        this.loadBalancer = loadBalancer;
    }

    public WebTarget app(final String appId) {
        String target = loadBalancer.getHomeUrl(appId).orElseThrow(serviceNotFound(appId));
        return client.target(target);
    }

    private Supplier<EurekaServiceNotFoundException> serviceNotFound(String appId) {
        return () -> new EurekaServiceNotFoundException(appId.toUpperCase());
    }
}
