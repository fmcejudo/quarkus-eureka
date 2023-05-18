/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.quarkus.eureka.registration;

import io.quarkus.eureka.client.Status;
import io.quarkus.eureka.exception.HealthCheckException;


import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;

import static io.quarkus.eureka.client.Status.DOWN;
import static io.quarkus.eureka.client.Status.UNKNOWN;
import static java.lang.String.format;
import static jakarta.ws.rs.core.Response.Status.Family.CLIENT_ERROR;
import static jakarta.ws.rs.core.Response.Status.Family.SUCCESSFUL;

class InstanceHealthCheckService {

    Status healthCheck(final String healthCheckUrl) {
        try (Client client = ClientBuilder.newClient(); Response response = client.target(healthCheckUrl)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get()) {
            if (response.getStatusInfo().getFamily().equals(CLIENT_ERROR)) {
                throw new HealthCheckException(
                        "Instance can't reach own application health check. Ensure this has been implemented"
                );
            }
            return getStatusFromResponse(response);

        } catch (ProcessingException ex) {
            throw new HealthCheckException(format("Health check not reachable: %s", healthCheckUrl), ex);
        }
    }

    private Status getStatusFromResponse(final Response response) {

        if (!response.getStatusInfo().getFamily().equals(SUCCESSFUL)) {
            return DOWN;
        }

        final Map<String, String> body = response.readEntity(Map.class);
        return body.entrySet()
                .stream()
                .filter(e -> e.getKey().equalsIgnoreCase("status"))
                .map(Map.Entry::getValue)
                .map(String::toUpperCase)
                .map(Status::valueOf)
                .findFirst().orElse(UNKNOWN);
    }

}
