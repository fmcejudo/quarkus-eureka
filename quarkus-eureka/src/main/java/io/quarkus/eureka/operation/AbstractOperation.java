package io.quarkus.eureka.operation;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;

import io.quarkus.eureka.config.Location;


public abstract class AbstractOperation implements Operation {

    protected Builder restClientBuilder(final Client client, final Location location, final String path) {

        Builder builder = client.target(String.join("/", location.getUrl(), path))
                .request(MediaType.APPLICATION_JSON_TYPE);

        setAuthHeaderIfNessary(builder, location);

        return builder;
    }

    private void setAuthHeaderIfNessary(Builder builder, Location location) {
    
        if (location.hasBasicAuthToken()) {
            builder.header("Authorization", "Basic " + location.getBasicAuthToken());
        }
    }    
}
