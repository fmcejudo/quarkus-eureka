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
import io.quarkus.eureka.util.HostNameDiscovery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.quarkus.eureka.util.HostNameDiscovery.getHostname;

public class HeartBeatOperationTest {

    private WireMockServer wireMockServer;

    private String serverUrl;

    private HeartBeatOperation heartBeatOperation;

    @BeforeEach
    public void setUp() {
        heartBeatOperation = new HeartBeatOperation();
        this.wireMockServer = new WireMockServer(8002);
        wireMockServer.start();

        this.serverUrl = String.format("http://localhost:%d", wireMockServer.port());
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void shouldUpdateInstanceWithPut() {
        //Given
        final String instanceId = getHostname() + ":" + "sample" + ":" + wireMockServer.port();
        final String updatePath = "/eureka/apps/SAMPLE/" + instanceId;
        wireMockServer.stubFor(put(urlEqualTo(updatePath))
                .willReturn(aResponse().withStatus(200)));

        InstanceInfo instanceInfo = InstanceInfo.of(TestInstanceInfoContext.of("SAMPLE", wireMockServer.port(),
                instanceId, getHostname()));

        //When
        heartBeatOperation.heartbeat(serverUrl.concat("/eureka"), instanceInfo);

        //Then
        wireMockServer.verify(1, putRequestedFor(urlEqualTo(updatePath)));

    }

    static class TestInstanceInfoContext implements InstanceInfoContext {

        private final String name;
        private final int port;
        private final String instanceId;
        private final String hostName;

        private TestInstanceInfoContext(final String name, final int port,
                                        final String instanceId, final String hostName) {
            this.name = name;
            this.port = port;
            this.instanceId = instanceId;
            this.hostName = hostName;
            HostNameDiscovery.setEurekaInstanceId(instanceId);
        }

        public static InstanceInfoContext of(final String name, final int port,
                                             final String instanceId, final String hostName) {
            return new TestInstanceInfoContext(name, port, instanceId, hostName);
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
        public String getInstanceId() {
            return instanceId;
        }

        @Override
        public String getHostName() {
            return hostName;
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