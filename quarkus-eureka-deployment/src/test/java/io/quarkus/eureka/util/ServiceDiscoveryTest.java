package io.quarkus.eureka.util;

import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.eureka.client.EurekaClient;
import io.quarkus.eureka.util.ServiceDiscoveryTest.ServiceDiscoveryTestProfile;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.WebTarget;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

@QuarkusTest
@TestProfile(ServiceDiscoveryTestProfile.class)
class ServiceDiscoveryTest {

    @Inject
    EurekaClient eurekaClient;

    private WireMockServer wireMockServer;

    private static final int PORT = 10034;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(PORT);
        wireMockServer.start();
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }


    @Test
    void shouldDiscoverServiceUrls() {
        //Given
        wireMockServer.stubFor(get(urlEqualTo("/eureka/apps/SAMPLE"))
            .willReturn(aResponse().withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBodyFile("instanceWithMetadata.json")));

        //When
        WebTarget sample = eurekaClient.app("SAMPLE");

        //Then
        Assertions.assertThat(sample.getUri().toString()).isEqualTo("http://10.34.37.227:9991/v2");

    }

    public static class ServiceDiscoveryTestProfile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of("quarkus.config.locations", "eureka-config.properties");
        }
    }

}