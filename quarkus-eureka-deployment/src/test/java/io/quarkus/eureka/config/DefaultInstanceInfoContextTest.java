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
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultInstanceInfoContextTest {

    private EurekaRuntimeConfiguration eurekaRuntimeConfiguration;
    private static final long HEALTH_CHECK_INITIAL_DELAY_DEFAULT = 3L;

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
        eurekaRuntimeConfiguration.healthCheckInitialDelay = HEALTH_CHECK_INITIAL_DELAY_DEFAULT;
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
        assertThat(instanceInfoContext.getHostName()).isEqualTo(HostNameDiscovery.getLocalHost());
    }

    @Test
    void shouldRegisterMetadata() {
        //Given
        eurekaRuntimeConfiguration.metadata = Map.of("tag", "v1", "app", "test-app");

        //When
        InstanceInfoContext instanceInfoContext = new DefaultInstanceInfoContext(eurekaRuntimeConfiguration);

        //Then
        assertThat(instanceInfoContext.getMetadata())
                .containsEntry("context", "/")
                .containsEntry("tag", "v1")
                .containsEntry("app", "test-app");
        Assertions.assertThat(eurekaRuntimeConfiguration.metadata).isNotEmpty();
        
    }

    @Test
    void shouldRegisterDefaultMetadata() {
        //When
        InstanceInfoContext instanceInfoContext = new DefaultInstanceInfoContext(eurekaRuntimeConfiguration);

        //Then
        assertThat(instanceInfoContext.getMetadata())
                .containsEntry("context", "/").doesNotContainKey("tag").doesNotContainKey("app");
        assertThat(eurekaRuntimeConfiguration.metadata).isNull();
        Assertions.assertThat(instanceInfoContext.getMetadata()).isNotEmpty();
    }

    @Test
    void shouldRegisterHealthCheckInitialDelay() {
        final long expected = HEALTH_CHECK_INITIAL_DELAY_DEFAULT + 4L;

        //Given
        eurekaRuntimeConfiguration.healthCheckInitialDelay = expected;

        //When
        InstanceInfoContext instanceInfoContext = new DefaultInstanceInfoContext(eurekaRuntimeConfiguration);

        //Then
        assertThat(instanceInfoContext.getHealthCheckInitialDelay())
                .isEqualTo(expected);
    }

    @Test
    void shouldRegisterDefaultHealthCheckInitialDelay() {

        //When
        InstanceInfoContext instanceInfoContext = new DefaultInstanceInfoContext(eurekaRuntimeConfiguration);

        //Then
        assertThat(instanceInfoContext.getHealthCheckInitialDelay())
                .isEqualTo(HEALTH_CHECK_INITIAL_DELAY_DEFAULT);

        assertThat(EurekaRuntimeConfiguration$$accessor.get_healthCheckInitialDelay(eurekaRuntimeConfiguration))
                .isEqualTo(HEALTH_CHECK_INITIAL_DELAY_DEFAULT);
    }
}