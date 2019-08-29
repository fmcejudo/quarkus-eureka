package io.quarkus.eureka.client;

import com.fasterxml.jackson.annotation.JsonProperty;


public class PortEnableInfo {

    @JsonProperty("$")
    private final String port;
    @JsonProperty("@enabled")
    private final boolean enabled;

    private PortEnableInfo(String port, boolean enabled) {
        this.port = port;
        this.enabled = enabled;
    }

    static PortEnableInfo of(final int port, final boolean enabled) {
        return new PortEnableInfo(String.valueOf(port), enabled);
    }

    public String getPort() {
        return port;
    }

    public String getEnabled() {
        return String.valueOf(this.enabled);
    }
}
