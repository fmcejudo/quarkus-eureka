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

package io.quarkus.eureka.client;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.quarkus.eureka.config.InstanceInfoContext;

import java.util.function.Function;

import static java.lang.String.format;

@JsonPropertyOrder({
        "instanceId",
        "hostName",
        "app",
        "vipAddress",
        "secureVipAddress",
        "ipAddr",
        "status",
        "homePageUrl",
        "statusPageUrl",
        "healthCheckUrl",
        "secureHealthCheckUrl",
        "port",
        "securePort",
        "dataCenterInfo"
})
public final class InstanceInfo {

    private final String instanceId;
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
        this.hostName = instanceInfoCtx.getHostName();
        this.app = instanceInfoCtx.getName().toUpperCase();
        this.vipAddress = instanceInfoCtx.getVipAddress();
        this.secureVipAddress = instanceInfoCtx.getVipAddress();
        this.ipAddr = instanceInfoCtx.getHostName();
        this.status = Status.STARTING;
        this.homePageUrl = buildUrl(instanceInfoCtx.getPort(), instanceInfoCtx.getHomePageUrl());
        this.statusPageUrl = buildUrl(instanceInfoCtx.getPort(), instanceInfoCtx.getStatusPageUrl());
        this.healthCheckUrl = buildUrl(instanceInfoCtx.getPort(), instanceInfoCtx.getHealthCheckUrl());
        this.secureHealthCheckUrl = buildUrl(instanceInfoCtx.getPort(), instanceInfoCtx.getHealthCheckUrl());
        this.port = PortEnableInfo.of(instanceInfoCtx.getPort(), true);
        this.securePort = PortEnableInfo.of(instanceInfoCtx.getPort(), false);
        this.dataCenterInfo = () -> DataCenterInfo.Name.MyOwn;
        this.instanceId = instanceInfoCtx.getInstanceId();
    }

    private InstanceInfo(final InstanceInfo instanceInfo, final Status status) {
        this.hostName = instanceInfo.getHostName();
        this.app = instanceInfo.getApp();
        this.vipAddress = instanceInfo.getVipAddress();
        this.secureVipAddress = instanceInfo.getSecureVipAddress();
        this.ipAddr = instanceInfo.getIpAddr();
        this.status = status;
        this.homePageUrl = instanceInfo.getHomePageUrl();
        this.statusPageUrl = instanceInfo.getStatusPageUrl();
        this.healthCheckUrl = instanceInfo.getHealthCheckUrl();
        this.secureHealthCheckUrl = instanceInfo.getSecureHealthCheckUrl();
        this.port = instanceInfo.getPort();
        this.securePort = instanceInfo.getSecurePort();
        this.dataCenterInfo = instanceInfo.getDataCenterInfo();
        this.instanceId = instanceInfo.getInstanceId();
    }

    private String buildUrl(final int port, final String resourcePath) {
        return Function.<String>identity()
                .andThen(path -> path.startsWith("/") ? path.substring(1) : path)
                .andThen(path -> format("http://%s:%d/%s", hostName, port, path))
                .apply(resourcePath);
    }

    public static InstanceInfo of(final InstanceInfoContext instanceInfoContext) {
        return new InstanceInfo(instanceInfoContext);
    }

    public String getInstanceId() {
        return instanceId;
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

    public InstanceInfo withStatus(final Status newStatus) {
        return new InstanceInfo(this, newStatus);
    }
}
