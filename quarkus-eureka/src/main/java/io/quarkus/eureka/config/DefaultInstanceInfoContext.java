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

public class DefaultInstanceInfoContext implements InstanceInfoContext {

    private final String name;
    private final int port;
    private final String vipAddress;
    private final String homePageUrl;
    private final String statusPageUrl;
    private final String healthCheckUrl;

    DefaultInstanceInfoContext(final EurekaConfiguration eurekaConfiguration) {
        this.name = eurekaConfiguration.name;
        this.port = eurekaConfiguration.port;
        this.vipAddress = eurekaConfiguration.vipAddress;
        this.homePageUrl = eurekaConfiguration.homePageUrl;
        this.statusPageUrl = eurekaConfiguration.statusPageUrl;
        this.healthCheckUrl = eurekaConfiguration.healthCheckUrl;
    }

    public static InstanceInfoContext withConfiguration(final EurekaConfiguration eurekaConfiguration) {
        return new DefaultInstanceInfoContext(eurekaConfiguration);
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

    public String getHomePageUrl() {
        return homePageUrl;
    }

    public String getStatusPageUrl() {
        return statusPageUrl;
    }

    public String getHealthCheckUrl() {
        return healthCheckUrl;
    }
}
