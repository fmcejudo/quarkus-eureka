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
import com.github.tomakehurst.wiremock.client.CountMatchingStrategy;
import io.quarkus.eureka.client.InstanceInfo;
import io.quarkus.eureka.config.InstanceInfoContext;
import io.quarkus.eureka.config.ServiceLocationConfig;
import io.quarkus.eureka.exception.HealthCheckException;
import io.quarkus.eureka.operation.OperationFactory;
import io.quarkus.eureka.operation.heartbeat.HeartBeatOperation;
import io.quarkus.eureka.operation.query.MultipleInstanceQueryOperation;
import io.quarkus.eureka.operation.register.RegisterOperation;
import io.quarkus.eureka.test.config.TestInstanceInfoContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class EurekaRegistrationServiceTest {

    private final String appName = "sample";

    private final static String hostname = "127.0.0.1";

    private final int port = 8002;

    private EurekaRegistrationService eurekaRegistrationService;

    private ScheduledExecutorService scheduledExecutorService;

    private MultipleInstanceQueryOperation multipleInstanceQueryOperation;

    private HeartBeatOperation heartBeatOperation;

    private RegisterOperation registerOperation;

    private WireMockServer wireMockServer;
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @BeforeEach
    void setUp() {
        logger.info("Starting mock server.");

        wireMockServer = new WireMockServer(port);
        wireMockServer.start();

        InstanceInfoContext instanceInfoContext = TestInstanceInfoContext.of(
                appName, port, appName, hostname, "/", "/v1", "/info/status", "/info/health", 3L
        );
        scheduledExecutorService = Mockito.mock(ScheduledExecutorService.class);
        registerOperation = new RegisterOperation();
        heartBeatOperation = new HeartBeatOperation();
        multipleInstanceQueryOperation = new MultipleInstanceQueryOperation();

        eurekaRegistrationService = new EurekaRegistrationService(
                new ServiceLocationConfig(singleton(
                        format("http://%s:%d/eureka", hostname, port))
                ),
                InstanceInfo.of(instanceInfoContext),
                new OperationFactory(asList(
                        registerOperation,
                        heartBeatOperation,
                        multipleInstanceQueryOperation
                )),
                scheduledExecutorService);

        when(scheduledExecutorService
                .scheduleWithFixedDelay(any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class))
        ).then(a -> {
            Runnable runnable = a.getArgument(0);
            runnable.run();
            return null;
        });
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void shouldRegisterAService() {
        logger.info("shouldRegisterAService test.");
        wireMockServer.stubFor(get(urlEqualTo("/info/health"))
                .willReturn(aResponse().withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("{\"status\" : \"up\"}")));

        wireMockServer.stubFor(get(urlEqualTo(join("/", "/eureka/apps", appName.toUpperCase())))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(404)
                ));

        wireMockServer.stubFor(post(urlEqualTo(join("/", "/eureka/apps", appName.toUpperCase())))
                .willReturn(aResponse().withHeader("Content-Type", "application/json").withStatus(204)));

        eurekaRegistrationService.register();

        wireMockServer.verify(
                new CountMatchingStrategy(CountMatchingStrategy.GREATER_THAN_OR_EQUAL,1),
                getRequestedFor(urlEqualTo("/info/health"))
        );
        wireMockServer.verify(1, getRequestedFor(urlEqualTo(join("/", "/eureka/apps", appName.toUpperCase()))));
        wireMockServer.verify(1, postRequestedFor(urlEqualTo(join("/", "/eureka/apps", appName.toUpperCase()))));
    }

    @Test
    void shouldFailWhenInstanceHealthCheckNotImplemented() {
        logger.info("shouldFailWhenInstanceHealthCheckNotImplemented test.");
        wireMockServer.stubFor(get(urlEqualTo("/info/health"))
                .willReturn(aResponse().withHeader("Content-Type", "application/json")
                        .withStatus(404)));

        Assertions.assertThatThrownBy(() -> eurekaRegistrationService.register())
                .isInstanceOf(HealthCheckException.class)
                .hasMessageContaining("Instance can't reach own application health check.");

        wireMockServer.verify(getRequestedFor(urlEqualTo("/info/health")));
    }

    @Test
    void shouldTryToRegisterWhenAppIsNotReachableInEureka() {
        logger.info("shouldTryToRegisterWhenAppIsNotReachableInEureka test.");
        wireMockServer.stubFor(get(urlEqualTo("/info/health"))
                .willReturn(aResponse().withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("{\"status\" : \"up\"}")));

        wireMockServer.stubFor(get(urlEqualTo(join("/", "/eureka/apps", appName.toUpperCase())))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(404)
                ));

        wireMockServer.stubFor(post(urlEqualTo(join("/", "/eureka/apps", appName.toUpperCase())))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(404)
                ));


        eurekaRegistrationService.register();

        wireMockServer.verify(
                new CountMatchingStrategy(CountMatchingStrategy.GREATER_THAN_OR_EQUAL,1),
                getRequestedFor(urlEqualTo("/info/health"))
        );

        wireMockServer.verify(1,
                getRequestedFor(urlEqualTo(String.join("/", "/eureka/apps", appName.toUpperCase())))
        );
        wireMockServer.verify(1,
                postRequestedFor(urlEqualTo(String.join("/", "/eureka/apps", appName.toUpperCase())))
        );

    }


    @Test
    void shouldHaveServiceRegistered() {
        logger.info("shouldHaveServiceRegistered test.");
        wireMockServer.stubFor(get(urlEqualTo("/info/health"))
                .willReturn(aResponse().withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("{\"status\" : \"up\"}")));

        wireMockServer.stubFor(get(urlEqualTo(join("/", "/eureka/apps", appName.toUpperCase())))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile("instancesByAppId2.json")
                ));

        String instanceId = join(":", hostname, appName, String.valueOf(port));
        wireMockServer.stubFor(put(urlEqualTo(join("/", "/eureka/apps", appName.toUpperCase(), instanceId)))
                .willReturn(aResponse().withHeader("Content-Type", "application/json").withStatus(200)));

        eurekaRegistrationService.register();

        wireMockServer.verify(
                new CountMatchingStrategy(CountMatchingStrategy.GREATER_THAN_OR_EQUAL,1),
                getRequestedFor(urlEqualTo("/info/health"))
        );

        wireMockServer.verify(1,
                getRequestedFor(urlEqualTo(String.join("/", "/eureka/apps", appName.toUpperCase())))
        );

        wireMockServer.verify(1,
                putRequestedFor(urlEqualTo(join("/", "/eureka/apps", appName.toUpperCase(), instanceId)))
        );

        wireMockServer.verify(0,
                postRequestedFor(urlEqualTo(join("/", "/eureka/apps", appName.toUpperCase())))
        );
    }

}