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

package io.quarkus.eureka.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

class HostNameDiscoveryTest {

    @BeforeEach
    void setUp() {
        HostNameDiscovery.resetHostname();
    }

    @Test
    @DisplayName("it should find a host ip")
    public void shouldReturnAHostAddress() {
        //Given && When
        String hostname = HostNameDiscovery.getHostname();

        //Then
        assertThat(hostname).doesNotContain(":");
    }

    @Test
    public void shouldSkipNetworkInterface() throws Exception {
        //Given && When
        final List<String> ignoredNetworkInterfaceNames = List.of("eth0", "en0");

        String skipIpAddress = Collections.list(NetworkInterface.getNetworkInterfaces()).stream()
                .filter(ni -> ignoredNetworkInterfaceNames.contains(ni.getDisplayName()))
                .findFirst().map(ni -> ni.getInetAddresses().nextElement().getHostAddress())
                .orElseThrow(() -> new RuntimeException(
                        format("none of the coming network interface names are found in your computer: %s",
                                String.join(",", ignoredNetworkInterfaceNames))
                ));

        String anotherIpAddress = HostNameDiscovery.getHostname(List.of("en0"));

        //Then
        Assertions.assertThat(anotherIpAddress).isNotEqualTo(skipIpAddress);
    }
}