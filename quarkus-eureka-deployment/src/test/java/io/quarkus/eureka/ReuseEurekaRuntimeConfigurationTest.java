package io.quarkus.eureka;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.eureka.client.EurekaClient;
import io.quarkus.test.QuarkusUnitTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.inject.Inject;
import javax.ws.rs.client.WebTarget;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

public class ReuseEurekaRuntimeConfigurationTest {

    @Inject
    EurekaClient eurekaClient;

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap
                    .create(JavaArchive.class)
                    .addAsResource("eureka-default-config.properties", "application.properties")
            );

    private WireMockServer wireMockServer;

    private static final int PORT = 10034;

    @BeforeEach
    public void setUp() {
        wireMockServer = new WireMockServer(PORT);
        wireMockServer.start();
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }

    @Test
    @DisplayName(value = "reading configuration properties for eureka")
    public void shouldLoadEurekaConfigAndRegisterBeans() {

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

}
