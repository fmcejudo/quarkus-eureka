package io.quarkus.eureka.operation.query;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

public class QueryOperationTest {

    private SingleInstanceQueryOperation singleInstanceQueryOperation;
    private MultipleInstanceQueryOperation multipleInstanceQueryOperation;

    private WireMockServer wireMockServer;

    private final Integer port;

    private final String location;

    {
        port = 10029;
        location = format("http://localhost:%d/eureka", port);
    }

    @BeforeEach
    public void setUp() {
        singleInstanceQueryOperation = new SingleInstanceQueryOperation();
        multipleInstanceQueryOperation = new MultipleInstanceQueryOperation();

        wireMockServer = new WireMockServer(port);
        wireMockServer.start();
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void shouldFindAllInstances() {
        wireMockServer.stubFor(get(urlEqualTo("/eureka/apps"))
                .willReturn(aResponse().withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile("allInstances.json")));
        ApplicationsResult applicationsResult = multipleInstanceQueryOperation.findAllInstances(location);
        assertThat(applicationsResult)
                .isInstanceOf(ApplicationsResult.class)
                .isNotNull();

        assertThat(applicationsResult.getApplications()).hasSize(1);

    }

    @Test
    public void shouldFindInstancesByAppID() {
        assertThat(multipleInstanceQueryOperation.findInstance(location, "appId"))
                .isInstanceOf(ApplicationResult.class).isNotNull();
    }

    @Test
    public void shouldFindInstanceByAppIDAndInstanceId() {
        assertThat(singleInstanceQueryOperation.findInstance(location, "appId", "instanceId"))
                .isInstanceOf(InstanceResult.class)
                .isNotNull();
    }

    @Test
    public void shouldFindInstanceById() {
        assertThat(singleInstanceQueryOperation.findInstanceById(location, "instanceId"))
                .isInstanceOf(InstanceResult.class).isNotNull();
    }
}