package io.quarkus.eureka.registration;

import io.quarkus.eureka.client.Status;
import io.quarkus.eureka.exception.HealthCheckException;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.logging.Logger;

import static io.quarkus.eureka.client.Status.DOWN;
import static io.quarkus.eureka.client.Status.UNKNOWN;
import static java.lang.String.format;
import static javax.ws.rs.core.Response.Status.Family.CLIENT_ERROR;
import static javax.ws.rs.core.Response.Status.Family.SUCCESSFUL;

class InstanceHealthCheckService {

    Status healthCheck(final String healthCheckUrl) {
        try {
            Response response = ResteasyClientBuilder.newClient()
                    .target(healthCheckUrl)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get();
            if (response.getStatusInfo().getFamily().equals(CLIENT_ERROR)) {
                throw new HealthCheckException(
                        "Instance can't reach own application health check. Ensure this has been implemented"
                );
            }
            return getStatusFromResponse(response);

        } catch (Exception ex) {
            throw new HealthCheckException(format("Health check not reachable: %s", healthCheckUrl), ex);
        }
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
