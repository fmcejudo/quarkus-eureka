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

package io.quarkus.eureka.operation.heartbeat;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.eureka.client.InstanceInfo;
import io.quarkus.eureka.config.InstanceInfoContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configure;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static io.quarkus.eureka.util.HostNameDiscovery.getInstanceId;

public class HeartBeatOperationTest {

    private WireMockServer wireMockServer;

    private String serverUrl;

    private HeartBeatOperation heartBeatOperation;

    @BeforeEach
    public void setUp() {
        heartBeatOperation = new HeartBeatOperation();
        this.wireMockServer = new WireMockServer(0);
        wireMockServer.start();

        this.serverUrl = String.format("http://localhost:%d", wireMockServer.port());
    }

    @Test
    public void shouldUpdateInstanceWithPut() {
        //Given
        final String updatePath = "/eureka/apps/SAMPLE/" + getInstanceId();
        wireMockServer.stubFor(put(urlEqualTo(updatePath))
                .willReturn(aResponse().withStatus(200)));

        InstanceInfo instanceInfo = InstanceInfo.of(TestInstanceInfoContext.of("SAMPLE", wireMockServer.port()));

        //When
        heartBeatOperation.heartbeat(serverUrl.concat("/eureka"), instanceInfo);

        //Then
        wireMockServer.verify(1, putRequestedFor(urlEqualTo(updatePath)));

    }

    static class TestInstanceInfoContext implements InstanceInfoContext {

        private final String name;
        private final int port;

        private TestInstanceInfoContext(final String name,
                                        final int port) {
            this.name = name;
            this.port = port;
        }

        public static InstanceInfoContext of(final String name,
                                             final int port) {
            return new TestInstanceInfoContext(name, port);
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
            return name;
        }

        @Override
        public String getHealthCheckUrl() {
            return "/info/health";
        }

        @Override
        public String getHomePageUrl() {
            return "/";
        }

        @Override
        public String getStatusPageUrl() {
            return "/info/status";
        }
    }

}