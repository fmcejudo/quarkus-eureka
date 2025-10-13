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

package io.quarkus.eureka;

import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.eureka.EurekaRuntimeConfigurationTest.EurekaRuntimeConfigurationTestProfile;
import io.quarkus.eureka.client.EurekaClient;
import io.quarkus.eureka.client.loadBalancer.LoadBalanced;
import io.quarkus.eureka.client.loadBalancer.LoadBalancerType;
import io.quarkus.eureka.exception.EurekaServiceNotFoundException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@QuarkusTest
@TestProfile(EurekaRuntimeConfigurationTestProfile.class)
class EurekaRuntimeConfigurationTest {

    @Inject
    public EurekaClient eurekaClient;

    @Inject
    @LoadBalanced(type = LoadBalancerType.ROUND_ROBIN)
    public EurekaClient eurekaClientRB;


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

        String instanceId = String.join(":", "localhost", "sample", String.valueOf(wireMockServer.port()));
        wireMockServer.stubFor(delete(urlEqualTo("/eureka/apps/SAMPLE/".concat(instanceId)))
                .willReturn(aResponse().withHeader("Content-Type", "application/json")
                        .withStatus(200)));

        wireMockServer.stubFor(get(urlEqualTo("/eureka/apps/SAMPLE"))
                .willReturn(aResponse().withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile("instancesByAppId.json")));

        assertThat(eurekaClient).isNotNull();

        WebTarget sampleWebTarget = eurekaClient.app("sample");
        assertThat(sampleWebTarget)
                .isNotNull()
                .extracting("uri").asString().isEqualTo("http://10.34.37.227:9991/");

        wireMockServer.verify(1, getRequestedFor(urlEqualTo("/eureka/apps/SAMPLE")));
    }

    @Test
    @DisplayName(value = "reading configuration properties for eureka with LB")
    void shouldLoadEurekaConfigAndRegisterBeansWithLB() {

        wireMockServer.stubFor(get(urlEqualTo("/eureka/apps/SAMPLE"))
                .willReturn(aResponse().withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile("instancesByAppId.json")));

        assertThat(eurekaClientRB).isNotNull();
        WebTarget sampleWebTarget = eurekaClientRB.app("sample");
        assertThat(sampleWebTarget)
                .isNotNull()
                .extracting("uri").asString().isEqualTo("http://10.34.37.227:9991/");
        WebTarget sampleWebTarget2 = eurekaClientRB.app("sample");
        assertThat(sampleWebTarget2)
                .isNotNull()
                .extracting("uri").asString().isEqualTo("http://10.34.37.227:9992/");
        WebTarget sampleWebTarget3 = eurekaClientRB.app("sample");
        assertThat(sampleWebTarget3)
                .isNotNull()
                .extracting("uri").asString().isEqualTo("http://10.34.37.227:9991/");

        wireMockServer.verify(1, getRequestedFor(urlEqualTo("/eureka/apps/SAMPLE")));
    }

    @Test
    void shouldFailWhenServiceNotFoundInEureka() {

        wireMockServer.stubFor(get(urlEqualTo("/eureka/apps/OTHER"))
                .willReturn(aResponse().withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile("downInstancesByAppId.json")));


        assertThatThrownBy(() -> eurekaClient.app("OTHER"))
                .isInstanceOf(EurekaServiceNotFoundException.class)
                .hasMessage("service OTHER not found");

        wireMockServer.verify(1, getRequestedFor(urlEqualTo("/eureka/apps/OTHER")));
    }

    public static class EurekaRuntimeConfigurationTestProfile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of("quarkus.config.locations", "eureka-config.properties");
        }
    }
}
