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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultInstanceInfoContextTest {

    private EurekaConfiguration eurekaConfiguration;

    @BeforeEach
    void setUp() {
        eurekaConfiguration = new EurekaConfiguration();
        eurekaConfiguration.port = 8001;
        eurekaConfiguration.name = "sample";
        eurekaConfiguration.vipAddress = "sample";
        eurekaConfiguration.region = "default";
        eurekaConfiguration.preferSameZone = true;
    }

    @Test
    void shouldBuildInstanceId() {
        //Given
        eurekaConfiguration.hostName = "example.com";

        //When
        InstanceInfoContext instanceInfoContext = new DefaultInstanceInfoContext(eurekaConfiguration);

        //Then
        assertThat(instanceInfoContext.getInstanceId()).isEqualTo("example.com:sample:8001");

    }

    @Test
    void shouldInstanceIdBeLowerCase() {
        //Given
        eurekaConfiguration.hostName = "EXAMPLE.COM";
        eurekaConfiguration.name = "SAMPLE";

        //When
        InstanceInfoContext instanceInfoContext = new DefaultInstanceInfoContext(eurekaConfiguration);

        //Then
        assertThat(instanceInfoContext.getInstanceId()).isEqualTo("example.com:sample:8001");
        assertThat(instanceInfoContext.getHostName()).isEqualTo("EXAMPLE.COM");

    }

    @Test
    void shouldGetDefinedHostname() {
        //Given
        eurekaConfiguration.hostName = "example.com";

        //When
        InstanceInfoContext instanceInfoContext = new DefaultInstanceInfoContext(eurekaConfiguration);

        //Then
        assertThat(instanceInfoContext.getHostName()).isEqualTo("example.com");
    }

    @Test
    void shouldGetHostAddress() {

        //Given && When
        InstanceInfoContext instanceInfoContext = new DefaultInstanceInfoContext(eurekaConfiguration);

        //Then
        assertThat(instanceInfoContext.getHostName()).isEqualTo(HostNameDiscovery.getHostname());
    }

}