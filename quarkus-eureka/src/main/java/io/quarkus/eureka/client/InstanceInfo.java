package io.quarkus.eureka.client;

import io.quarkus.eureka.config.InstanceInfoContext;

import java.util.function.Function;

import static io.quarkus.eureka.util.HostNameDiscovery.getHostname;
import static java.lang.String.format;

public final class InstanceInfo {

    private final String hostName;
    private final String app;
    private final String vipAddress;
    private final String secureVipAddress;
    private final String ipAddr;
    private final Status status;
    private final String homePageUrl;
    private final String statusPageUrl;
    private final String healthCheckUrl;
    private final String secureHealthCheckUrl;
    private final PortEnableInfo port;
    private final PortEnableInfo securePort;
    private final DataCenterInfo dataCenterInfo;

    private InstanceInfo(final InstanceInfoContext instanceInfoCtx) {
        this.hostName = getHostname();
        this.app = instanceInfoCtx.getName().toUpperCase();
        this.vipAddress = instanceInfoCtx.getVipAddress();
        this.secureVipAddress = instanceInfoCtx.getVipAddress();
        this.ipAddr = getHostname();
        this.status = Status.UP;
        this.homePageUrl = buildUrl(instanceInfoCtx.getPort(), instanceInfoCtx.getHomePageUrl());
        this.statusPageUrl = buildUrl(instanceInfoCtx.getPort(), instanceInfoCtx.getStatusPageUrl());
        this.healthCheckUrl = buildUrl(instanceInfoCtx.getPort(), instanceInfoCtx.getHealthCheckUrl());
        this.secureHealthCheckUrl = buildUrl(instanceInfoCtx.getPort(), instanceInfoCtx.getHealthCheckUrl());
        this.port = PortEnableInfo.of(instanceInfoCtx.getPort(), true);
        this.securePort = PortEnableInfo.of(instanceInfoCtx.getPort(), false);
        this.dataCenterInfo = () -> DataCenterInfo.Name.MyOwn;
    }

    private String buildUrl(final int port, final String resourcePath) {
        return Function.<String>identity()
                .andThen(path -> path.startsWith("/") ? path.substring(1) : path)
                .andThen(path -> format("http://%s:%d/%s", getHostname(), port, path))
                .apply(resourcePath);
    }

    public static InstanceInfo of(final InstanceInfoContext instanceInfoContext) {
        return new InstanceInfo(instanceInfoContext);
    }

    public String getHostName() {
        return hostName;
    }

    public String getApp() {
        return app;
    }

    public String getVipAddress() {
        return vipAddress;
    }

    public String getSecureVipAddress() {
        return secureVipAddress;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public Status getStatus() {
        return status;
    }

    public String getHomePageUrl() {
        return homePageUrl;
    }

    public String getStatusPageUrl() {
        return statusPageUrl;
    }

    public String getHealthCheckUrl() {
        return healthCheckUrl;
    }

    public String getSecureHealthCheckUrl() {
        return secureHealthCheckUrl;
    }

    public PortEnableInfo getPort() {
        return port;
    }

    public PortEnableInfo getSecurePort() {
        return securePort;
    }

    public DataCenterInfo getDataCenterInfo() {
        return dataCenterInfo;
    }

}
