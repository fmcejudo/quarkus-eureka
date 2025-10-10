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

import java.util.Map;

import io.quarkus.eureka.config.DefaultInstanceInfoContextTest.DefaultInstanceInfoContextTestProfile;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@TestProfile(DefaultInstanceInfoContextTestProfile.class)
class DefaultInstanceInfoContextTest {

    InstanceInfoContext instanceInfoContext;

    @Inject
    EurekaRuntimeConfiguration eurekaRuntimeConfiguration;


    @Test
    void shouldLoadInstanceInfoContext() {
        //Given


        //When
        instanceInfoContext = new DefaultInstanceInfoContext(eurekaRuntimeConfiguration);

        //Then
        assertThat(eurekaRuntimeConfiguration).isNotNull();
        assertThat(instanceInfoContext.getInstanceId()).contains(":sample:8001");
        assertThat(instanceInfoContext.getPort()).isEqualTo(8001);
        assertThat(instanceInfoContext.getHealthCheckInitialDelay()).isZero();
        Assertions.assertThat(instanceInfoContext.getHealthCheckUrl()).isEqualTo("/info/health");

    }


    public static class DefaultInstanceInfoContextTestProfile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of("quarkus.config.locations", "eureka-config.properties");
        }
    }

}