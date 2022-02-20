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

package io.quarkus.eureka.config;

import io.quarkus.eureka.util.HostNameDiscovery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;
import static org.assertj.core.api.Assertions.assertThat;

class DefaultInstanceInfoContextTest {

    private EurekaRuntimeConfiguration eurekaRuntimeConfiguration;

    @BeforeEach
    void setUp() {
        eurekaRuntimeConfiguration = new EurekaRuntimeConfiguration();
        eurekaRuntimeConfiguration.port = 8001;
        eurekaRuntimeConfiguration.name = "sample";
        eurekaRuntimeConfiguration.vipAddress = "sample";
        eurekaRuntimeConfiguration.region = "default";
        eurekaRuntimeConfiguration.preferSameZone = true;
        eurekaRuntimeConfiguration.hostName = HostNameDiscovery.getHostname();
        eurekaRuntimeConfiguration.contextPath = "/";
        HostNameDiscovery.resetHostname();
    }

    @Test
    void shouldBuildInstanceId() {
        //Given
        eurekaRuntimeConfiguration.hostName = "example.com";

        //When
        InstanceInfoContext instanceInfoContext = new DefaultInstanceInfoContext(eurekaRuntimeConfiguration);

        //Then
        assertThat(instanceInfoContext.getInstanceId()).isEqualTo("example.com:sample:8001");

    }

    @Test
    void shouldInstanceIdBeLowerCase() {
        //Given
        eurekaRuntimeConfiguration.hostName = "EXAMPLE.COM";
        eurekaRuntimeConfiguration.name = "SAMPLE";

        //When
        InstanceInfoContext instanceInfoContext = new DefaultInstanceInfoContext(eurekaRuntimeConfiguration);

        //Then
        assertThat(instanceInfoContext.getInstanceId()).isEqualTo("example.com:sample:8001");
        assertThat(instanceInfoContext.getHostName()).isEqualTo("EXAMPLE.COM");

    }

    @Test
    void shouldGetDefinedHostname() {
        //Given
        eurekaRuntimeConfiguration.hostName = "example.com";

        //When
        InstanceInfoContext instanceInfoContext = new DefaultInstanceInfoContext(eurekaRuntimeConfiguration);

        //Then
        assertThat(instanceInfoContext.getHostName()).isEqualTo("example.com");
    }

    @Test
    void shouldGetHostAddress() {

        //Given && When
        InstanceInfoContext instanceInfoContext = new DefaultInstanceInfoContext(eurekaRuntimeConfiguration);

        //Then
        assertThat(instanceInfoContext.getHostName()).isEqualTo(HostNameDiscovery.getHostname());
    }

    @Test
    void shouldGetLocalAddress() {
    	eurekaRuntimeConfiguration.preferIpAddress = true;

        //Given && When
        InstanceInfoContext instanceInfoContext = new DefaultInstanceInfoContext(eurekaRuntimeConfiguration);

        //Then
        assertThat(instanceInfoContext.getHostName())
                .isNotNull()
                .doesNotStartWith("127.")
                .matches("\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}");
    }

    @Test
    void shouldIgnoreDefaultNetworkInterface() throws Exception {
        // Given
        Predicate<NetworkInterface> isLoopbackInterface = ni -> {
            try {
                return ni.isLoopback();
            } catch (Exception exception) {
                return false;
            }
        };

        List<String> nonLoopbackInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces()).stream()
                .filter(not(isLoopbackInterface))
                .map(NetworkInterface::getDisplayName)
                .collect(Collectors.toList());

        eurekaRuntimeConfiguration.preferIpAddress = true;
        eurekaRuntimeConfiguration.ignoreNetworkInterfaces = String.join(",", nonLoopbackInterfaces);

        // When
        InstanceInfoContext instanceInfoContext = new DefaultInstanceInfoContext(eurekaRuntimeConfiguration);

        // Then
        assertThat(instanceInfoContext.getHostName())
                .isNotNull()
                .isEqualTo("127.0.0.1");
    }
}