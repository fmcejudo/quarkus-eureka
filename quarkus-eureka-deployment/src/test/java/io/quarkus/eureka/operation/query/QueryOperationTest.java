package io.quarkus.eureka.operation.query;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.eureka.client.Status;
import org.jboss.resteasy.spi.NotImplementedYetException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

        List<ApplicationResult> applications = applicationsResult.getApplications();
        assertThat(applications).hasSize(1);
        assertThat(applications.get(0))
                .extracting("name").isEqualTo("SAMPLE");
        List<InstanceResult> instanceResults = applications.get(0).getInstanceResults();
        assertThat(instanceResults).hasSize(1)
                .flatExtracting("status", "app").containsExactly(Status.UP, "SAMPLE");

    }

    @Test
    public void shouldFindInstancesByAppID() {
        wireMockServer.stubFor(get(urlEqualTo("/eureka/apps/SAMPLE"))
                .willReturn(aResponse().withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile("instancesByAppId.json")));

        assertThat(multipleInstanceQueryOperation.findInstance(location, "SAMPLE"))
                .isInstanceOf(ApplicationResult.class).isNotNull();
    }

    @Test
    public void shouldFindInstanceByAppIDAndInstanceId() {
        assertThatThrownBy(() -> singleInstanceQueryOperation.findInstance(location, "SAMPLE", "10.34.37.227"))
                .isInstanceOf(NotImplementedYetException.class);
    }

    @Test
    public void shouldFindInstanceById() {
        assertThatThrownBy(() -> singleInstanceQueryOperation.findInstanceById(location, "10.34.37.227"))
                .isInstanceOf(NotImplementedYetException.class);
    }
}