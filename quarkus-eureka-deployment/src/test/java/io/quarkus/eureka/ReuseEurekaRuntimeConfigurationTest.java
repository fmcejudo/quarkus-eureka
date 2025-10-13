package io.quarkus.eureka;

import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.eureka.ReuseEurekaRuntimeConfigurationTest.ReuseEurekaRuntimeConfigurationTestProfile;
import io.quarkus.eureka.client.EurekaClient;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.WebTarget;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@TestProfile(ReuseEurekaRuntimeConfigurationTestProfile.class)
class ReuseEurekaRuntimeConfigurationTest {

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
    @DisplayName(value = "reading configuration properties for eureka")
    void shouldLoadEurekaConfigAndRegisterBeans() {

        String instanceId = String.join(":", "localhost", "application-name", String.valueOf(wireMockServer.port()));
        wireMockServer.stubFor(delete(urlEqualTo("/eureka/apps/APPLICATION-NAME/".concat(instanceId)))
            .willReturn(aResponse().withHeader("Content-Type", "application/json")
                .withStatus(200)));

        wireMockServer.stubFor(get(urlEqualTo("/eureka/apps/APPLICATION-NAME"))
            .willReturn(aResponse().withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBodyFile("instancesByApplicationName.json")));

        assertThat(eurekaClient).isNotNull();

        WebTarget sampleWebTarget = eurekaClient.app("application-name");
        assertThat(sampleWebTarget)
            .isNotNull()
            .extracting("uri").asString().isEqualTo("http://10.34.37.227:8002/");

        wireMockServer.verify(1, getRequestedFor(urlEqualTo("/eureka/apps/APPLICATION-NAME")));
    }

    public static class ReuseEurekaRuntimeConfigurationTestProfile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of("quarkus.config.locations", "eureka-default-config.properties,application.properties");
        }
    }

}
