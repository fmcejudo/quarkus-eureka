package io.quarkus.eureka.operation.remove;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.quarkus.eureka.util.HostNameDiscovery.getHostname;

class RemoveInstanceOperationTest {

    private RemoveInstanceOperation removeInstanceOperation;

    private WireMockServer wireMockServer;

    private String serverUrl;


    @BeforeEach
    public void setUp() {

        this.removeInstanceOperation = new RemoveInstanceOperation();
        this.wireMockServer = new WireMockServer(0);
        wireMockServer.start();

        this.serverUrl = String.format("http://localhost:%d", wireMockServer.port());

    }

    @Test
    public void shouldCallDeleteToRemoveInstance() {
        //Given
        final String deletePath = "/eureka/apps/SAMPLE/" + getHostname() + ":" + "sample" + ":" + 8001;
        wireMockServer.stubFor(delete(urlEqualTo(deletePath))
                .willReturn(aResponse().withStatus(200)));


        //When
        removeInstanceOperation.remove(serverUrl.concat("/eureka"), "SAMPLE");

        //Then
        wireMockServer.verify(1, deleteRequestedFor(urlEqualTo(deletePath)));
    }

}