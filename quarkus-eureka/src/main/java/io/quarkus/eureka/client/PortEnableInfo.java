package io.quarkus.eureka.client;

public class PortEnableInfo {

    private final String port;
    private final boolean enabled;

    private PortEnableInfo(String port, boolean enabled) {
        this.port = port;
        this.enabled = enabled;
    }

    public static PortEnableInfo of(final int port, final boolean enabled) {
        return new PortEnableInfo(String.valueOf(port), enabled);
    }

    public String getPort() {
        return port;
    }

    public String getEnabled() {
        return String.valueOf(this.enabled);
    }
}
