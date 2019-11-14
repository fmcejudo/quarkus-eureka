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

package io.quarkus.eureka.operation.remove;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.eureka.util.HostNameDiscovery;
import org.junit.jupiter.api.AfterEach;
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
        this.wireMockServer = new WireMockServer(8002);
        wireMockServer.start();

        this.serverUrl = String.format("http://localhost:%d", wireMockServer.port());

    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void shouldCallDeleteToRemoveInstance() {
        //Given
        final String deletePath = "/eureka/apps/SAMPLE/" + getHostname() + ":" + "sample" + ":" + wireMockServer.port();
        HostNameDiscovery.setEurekaInstanceId(getHostname()+ ":" + "sample" + ":" + wireMockServer.port());
        wireMockServer.stubFor(delete(urlEqualTo(deletePath))
                .willReturn(aResponse().withStatus(200)));


        //When
        removeInstanceOperation.remove(serverUrl.concat("/eureka"), "SAMPLE");

        //Then
        wireMockServer.verify(1, deleteRequestedFor(urlEqualTo(deletePath)));
    }

}