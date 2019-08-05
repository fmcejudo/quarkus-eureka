package io.quarkus.eureka.client;

import io.quarkus.eureka.config.EurekaConfiguration;

import static io.quarkus.eureka.util.HostNameDiscovery.getHostname;
import static java.lang.String.format;

public class InstanceInfo {

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

    public InstanceInfo(final EurekaConfiguration eurekaConfiguration) {
        this.hostName = getHostname();
        this.app = eurekaConfiguration.getName().toUpperCase();
        this.vipAddress = eurekaConfiguration.getVipAddress();
        this.secureVipAddress = eurekaConfiguration.getVipAddress();
        this.ipAddr = getHostname();
        this.status = Status.UP;
        this.homePageUrl = format(
                "http://%s:%d/%s",
                getHostname(),
                eurekaConfiguration.getPort(),
                eurekaConfiguration.getHomePageUrl()
        );
        this.statusPageUrl = format(
                "http://%s:%d/%s",
                getHostname(),
                eurekaConfiguration.getPort(),
                eurekaConfiguration.getStatusPageUrl()
        );
        this.healthCheckUrl = format(
                "http://%s:%d/%s",
                getHostname(),
                eurekaConfiguration.getPort(),
                eurekaConfiguration.getHealthCheckUrl()
        );
        this.secureHealthCheckUrl = this.healthCheckUrl;
        this.port = PortEnableInfo.of(eurekaConfiguration.getPort(), true);
        this.securePort = PortEnableInfo.of(eurekaConfiguration.getPort(), false);
        this.dataCenterInfo = () -> DataCenterInfo.Name.MyOwn;
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
