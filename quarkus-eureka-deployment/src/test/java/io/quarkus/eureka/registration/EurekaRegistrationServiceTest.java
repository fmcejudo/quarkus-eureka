package io.quarkus.eureka.registration;


import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.eureka.client.InstanceInfo;
import io.quarkus.eureka.config.InstanceInfoContext;
import io.quarkus.eureka.config.ServiceLocationConfig;
import io.quarkus.eureka.operation.OperationFactory;
import io.quarkus.eureka.operation.query.MultipleInstanceQueryOperation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.quarkus.eureka.util.HostNameDiscovery.getHostname;
import static java.lang.String.format;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class EurekaRegistrationServiceTest {

    private final String appName = "quarkus-eureka-test";

    private final int port = 10099;

    private EurekaRegistrationService eurekaRegistrationService;

    private ScheduledExecutorService scheduledExecutorService;

    private MultipleInstanceQueryOperation multipleInstanceQueryOperation;

    private WireMockServer wireMockServer;

    @BeforeEach
    public void setUp() {
        wireMockServer = new WireMockServer(port);
        wireMockServer.start();

        InstanceInfoContext instanceInfoContext = new TestInstanceInfoContext(
                appName.toUpperCase(), port, appName, "/", "/info/status", "/info/health"
        );
        scheduledExecutorService = Mockito.mock(ScheduledExecutorService.class);
        multipleInstanceQueryOperation = Mockito.mock(MultipleInstanceQueryOperation.class);

        when(multipleInstanceQueryOperation.findInstance(anyString(), anyString())).thenCallRealMethod();
        when(multipleInstanceQueryOperation.query(anyString(), anyString(), any(Class.class))).thenCallRealMethod();

        eurekaRegistrationService = new EurekaRegistrationService(
                new ServiceLocationConfig(singleton(
                        format("http://%s:%d/eureka", getHostname(), port))
                ),
                InstanceInfo.of(instanceInfoContext),
                new OperationFactory(singletonList(multipleInstanceQueryOperation)),
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
    public void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void shouldRegisterAService() {
        wireMockServer.stubFor(get(urlEqualTo("/info/health"))
                .willReturn(aResponse().withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("{\"status\" : \"up\"}")));

        wireMockServer.stubFor(get(urlEqualTo("/eureka/apps/" + appName.toUpperCase()))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile("instancesByAppId.json")
                ));

        wireMockServer.stubFor(post(urlEqualTo("/eureka/apps/" + appName.toUpperCase()))
                .willReturn(aResponse().withHeader("Content-Type", "application/json").withStatus(204)));

        eurekaRegistrationService.register();

        wireMockServer.verify(1, getRequestedFor(urlEqualTo("/info/health")));
        wireMockServer.verify(1, getRequestedFor(urlEqualTo("/eureka/apps/" + appName.toUpperCase())));
        wireMockServer.verify(1, postRequestedFor(urlEqualTo("/eureka/apps/" + appName.toUpperCase())));
    }

    static class TestInstanceInfoContext implements InstanceInfoContext {
        private final String name;
        private final int port;
        private final String vipAddress;
        private final String homePageUrl;
        private final String statusPageUrl;
        private final String healthCheckUrl;

        TestInstanceInfoContext(String name, int port, String vipAddress,
                                String homePageUrl, String statusPageUrl, String healthCheckUrl) {
            this.name = name;
            this.port = port;
            this.vipAddress = vipAddress;
            this.homePageUrl = homePageUrl;
            this.statusPageUrl = statusPageUrl;
            this.healthCheckUrl = healthCheckUrl;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getPort() {
            return port;
        }

        @Override
        public String getVipAddress() {
            return vipAddress;
        }

        @Override
        public String getHomePageUrl() {
            return homePageUrl;
        }

        @Override
        public String getStatusPageUrl() {
            return statusPageUrl;
        }

        @Override
        public String getHealthCheckUrl() {
            return healthCheckUrl;
        }
    }


}