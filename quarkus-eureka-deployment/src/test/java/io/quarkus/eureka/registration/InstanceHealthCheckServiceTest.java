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

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.eureka.client.Status;
import io.quarkus.eureka.exception.HealthCheckException;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static java.lang.String.join;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class InstanceHealthCheckServiceTest {

    private final String healthPath;

    {
        healthPath = "/info/health";
    }

    private InstanceHealthCheckService instanceHealthCheckService;

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void onInit() {
        wireMockServer = new WireMockServer(0);
        wireMockServer.start();
    }

    @AfterAll
    static void onFinish() {
        wireMockServer.stop();
    }

    @BeforeEach
    public void setUp() {
        instanceHealthCheckService = new InstanceHealthCheckService();
    }

    @Test
    public void shouldReachHealthCheck() {

        wireMockServer.stubFor(get(urlEqualTo(healthPath))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .withStatus(200)
                        .withBody("""
                          {"status": "UP"}
                        """)));

        Status status = instanceHealthCheckService.healthCheck(wireMockServer.baseUrl().concat(healthPath));
        assertThat(status).isEqualTo(Status.UP);
    }

    @Test
    public void shouldThrowAnExceptionWhenHealthCheckNoImplemented() {

        wireMockServer.stubFor(get(urlEqualTo(healthPath))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(404)));

        assertThatThrownBy(() -> instanceHealthCheckService.healthCheck(wireMockServer.baseUrl().concat(healthPath)))
                .isInstanceOf(HealthCheckException.class)
                .hasMessageContaining("Instance can't reach own application health check.");

    }

    @Test
    public void shouldWrapExceptionWithHealthCheckException() {

        assertThatThrownBy(() -> instanceHealthCheckService.healthCheck(join("", "http://wrong-server", healthPath)))
                .isInstanceOf(HealthCheckException.class)
                .hasMessageContaining("Health check not reachable:");
    }

}