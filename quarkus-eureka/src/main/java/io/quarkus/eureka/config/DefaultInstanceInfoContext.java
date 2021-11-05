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

import java.util.LinkedHashMap;
import java.util.Map;

import static java.lang.String.join;

import io.quarkus.eureka.util.HostNameDiscovery;

public class DefaultInstanceInfoContext implements InstanceInfoContext {

    private final String name;
    private final int port;
    private final String vipAddress;
    private final String instanceId;
    private final String hostName;
    private final String homePageUrl;
    private final String statusPageUrl;
    private final String healthCheckUrl;
    private final Map<String, String> metadata;

    DefaultInstanceInfoContext(final EurekaRuntimeConfiguration eurekaRuntimeConfiguration) {
        this.name = eurekaRuntimeConfiguration.name;
        this.port = eurekaRuntimeConfiguration.port;
        this.vipAddress = eurekaRuntimeConfiguration.vipAddress;
        this.homePageUrl = eurekaRuntimeConfiguration.homePageUrl;
        this.statusPageUrl = eurekaRuntimeConfiguration.statusPageUrl;
        this.healthCheckUrl = eurekaRuntimeConfiguration.healthCheckUrl;
        this.hostName = eurekaRuntimeConfiguration.preferIpAddress
            ? HostNameDiscovery.getLocalHost()
            : eurekaRuntimeConfiguration.hostName;
        this.instanceId = buildInstanceId();
        this.metadata = new LinkedHashMap<>(Map.of("context", eurekaRuntimeConfiguration.contextPath));
    }

    public static InstanceInfoContext withConfiguration(final EurekaRuntimeConfiguration eurekaRuntimeConfiguration) {
        return new DefaultInstanceInfoContext(eurekaRuntimeConfiguration);
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }

    public String getVipAddress() {
        return vipAddress;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getHostName() {
        return hostName;
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

    public Map<String, String> getMetadata() {
        return this.metadata;
    }

    private String buildInstanceId() {
        return join(":", this.getHostName(), this.getName(), String.valueOf(this.getPort())).toLowerCase();
    }

}
