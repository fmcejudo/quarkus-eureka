package io.quarkus.eureka.operation;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;


public abstract class AbstractOperation implements Operation {

    protected Builder restClientBuilder(final Client client, final String location, final String path) {
        if (isSecureLocation(location)) {
            //change to https if it is not defined so and add a header
            return client.target(String.join("/", location.replace("http", "https"), path))
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .header("Authorization", "something in here");
        }
        return client.target(String.join("/", location, path))
                .request(MediaType.APPLICATION_JSON_TYPE);
    }

    private boolean isSecureLocation(final String location) {
        return false; //location should match http[s]://user@password:location
    }
}
