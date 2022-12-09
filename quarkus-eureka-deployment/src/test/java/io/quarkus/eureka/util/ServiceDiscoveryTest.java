package io.quarkus.eureka.util;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.eureka.client.EurekaClient;
import io.quarkus.test.QuarkusUnitTest;
import org.assertj.core.api.Assertions;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.inject.Inject;
import javax.ws.rs.client.WebTarget;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

class ServiceDiscoveryTest {

    @Inject
    EurekaClient eurekaClient;

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
        .setArchiveProducer(() -> ShrinkWrap
            .create(JavaArchive.class)
            .addAsResource("eureka-config.properties", "application.properties")
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

}