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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.eureka.client.Status;
import io.quarkus.eureka.exception.HealthCheckException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static io.quarkus.eureka.client.Status.UNKNOWN;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

class InstanceHealthCheckService {

    Status healthCheck(final String healthCheckUrl) {

        try (HttpClient httpClient = HttpClient.newBuilder().build()) {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(healthCheckUrl)).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));

            if ((response.statusCode() & 200) == 200) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, String> result = mapper.readValue(response.body(), Map.class);
                return getStatusFromResponse(result);
            }

            throw new HealthCheckException(
                    "Instance can't reach own application health check. Ensure this has been implemented"
            );
        } catch (HealthCheckException e) {
            throw e;
        } catch (Exception ex) {
            throw new HealthCheckException(format("Health check not reachable: %s", healthCheckUrl), ex);
        }

    }

    private Status getStatusFromResponse(final Map<String, String> body) {

        return body.entrySet()
                .stream()
                .filter(e -> e.getKey().equalsIgnoreCase("status"))
                .map(Map.Entry::getValue)
                .map(String::toUpperCase)
                .map(Status::valueOf)
                .findFirst().orElse(UNKNOWN);
    }

}
