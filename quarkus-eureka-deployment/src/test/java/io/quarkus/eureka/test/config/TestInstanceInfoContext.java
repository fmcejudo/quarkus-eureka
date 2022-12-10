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

package io.quarkus.eureka.test.config;

import io.quarkus.eureka.config.InstanceInfoContext;

import java.util.Map;

import static java.lang.String.join;

public class TestInstanceInfoContext implements InstanceInfoContext {

    private final String name;
    private final int port;
    private final String vipAddress;
    private final String instanceId;
    private final String hostName;
    private final String homePageUrl;
    private final String statusPageUrl;
    private final String healthCheckUrl;
    private final Map<String, String> metadata;
    private final long healthCheckInitialDelay;

    private TestInstanceInfoContext(String name, int port, String vipAddress, String hostName,
        String homePageUrl, String contextPath, String statusPageUrl, String healthCheckUrl, long healthCheckInitialDelay) {
        this.name = name;
        this.port = port;
        this.vipAddress = vipAddress;
        this.homePageUrl = homePageUrl;
        this.statusPageUrl = statusPageUrl;
        this.healthCheckUrl = healthCheckUrl;
        this.hostName = hostName;
        this.instanceId = buildInstanceId();
        this.metadata = Map.of("context", contextPath);
        this.healthCheckInitialDelay = healthCheckInitialDelay;
    }

    public static InstanceInfoContext of(final String name, final int port, final String hostName) {

        return new TestInstanceInfoContext(name, port, name, hostName, "/", "/", "/info/status", "/info/health", 3L);
    }

    public static InstanceInfoContext of(final String name, final int port, final String vipAddress,
        final String hostName, final String homePageUrl, final String contextPath,
        final String statusPageUrl, final String healthCheckUrl, final long healthCheckInitialDelay) {
        return new TestInstanceInfoContext(
            name, port, vipAddress, hostName, homePageUrl, contextPath, statusPageUrl, healthCheckUrl, healthCheckInitialDelay
        );
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getVipAddress() {
        return vipAddress;
    }

    @Override
    public String getInstanceId() {
        return instanceId;
    }

    @Override
    public String getHostName() {
        return hostName;
    }

    @Override
    public String getHomePageUrl() {
        return homePageUrl;
    }

    @Override
    public String getStatusPageUrl() {
        return statusPageUrl;
    }

    @Override
    public String getHealthCheckUrl() {
        return healthCheckUrl;
    }

    public Map<String, String> getMetadata() {
        return this.metadata;
    }

    private String buildInstanceId() {
        return join(":", this.getHostName(), this.getName(), String.valueOf(this.getPort())).toLowerCase();
    }

    @Override
    public long getHealthCheckInitialDelay() {
        return healthCheckInitialDelay;
    }
}
