package io.quarkus.eureka.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HostNameDiscoveryTest {

    @Test
    @DisplayName("it should find a host ip")
    public void shouldReturnAHostAddress() {
        String hostname = HostNameDiscovery.getHostname();
        assertThat(hostname).doesNotContain(":");
    }
}