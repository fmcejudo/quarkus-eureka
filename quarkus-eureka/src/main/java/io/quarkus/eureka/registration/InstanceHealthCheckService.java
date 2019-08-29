package io.quarkus.eureka.registration;

import io.quarkus.eureka.client.Status;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

import static io.quarkus.eureka.client.Status.DOWN;
import static io.quarkus.eureka.client.Status.UNKNOWN;
import static javax.ws.rs.core.Response.Status.Family.SUCCESSFUL;

class InstanceHealthCheckService {

    Status healthCheck(final String healthCheckUrl) {
        Response response = ResteasyClientBuilder.newClient()
                .target(healthCheckUrl)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();
        return getStatusFromResponse(response);
    }

    private Status getStatusFromResponse(final Response response) {

        if (!response.getStatusInfo().getFamily().equals(SUCCESSFUL)) {
            return DOWN;
        }

        Map<String, String> body = response.readEntity(Map.class);
        return body.entrySet()
                .stream()
                .filter(e -> e.getKey().equalsIgnoreCase("status"))
                .map(Map.Entry::getValue)
                .map(String::toUpperCase)
                .map(Status::valueOf)
                .findFirst().orElse(UNKNOWN);
    }

}
