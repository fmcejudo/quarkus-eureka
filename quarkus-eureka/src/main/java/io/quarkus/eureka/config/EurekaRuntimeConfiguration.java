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

import io.quarkus.eureka.util.HostNameDiscovery;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.quarkus.runtime.annotations.ConvertWith;
import org.eclipse.microprofile.config.spi.Converter;

import java.util.Map;

@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public class EurekaRuntimeConfiguration {

    /**
     * enable the eureka client
     */
    @ConfigItem(defaultValue = "true")
    public boolean enable;

    /**
     * port where eureka server will redirect to attend the service
     */
    @ConfigItem(defaultValue = "${quarkus.http.port}")
    Integer port;

    /**
     * name of the service as it will turn up in Eureka
     */
    @ConfigItem(defaultValue = "${quarkus.application.name}")
    String name;

    /**
     * base-path where the application resides
     */
    @ConfigItem(defaultValue = "${quarkus.http.root-path:/}")
    String contextPath;

    /**
     * Name used by load balancer to redirect to the service
     */
    @ConfigItem(defaultValue = "${quarkus.application.name}")
    String vipAddress;

    /**
     * The hostname, otherwise it will be guest from OS primitives
     */
    @ConvertWith(NetworkConverter.class)
    @ConfigItem
    String hostName;

    /**
     * Determines if the local ip address should be used instead of the hostName.
     */
    @ConfigItem
    boolean preferIpAddress;

    /**
     * if AWS environment, in which region this registry service is
     */
    @ConfigItem(defaultValue = "default")
    String region;

    /**
     * instances of registry services in which the application will publish itself
     */
    @ConfigItem
    Map<String, String> serviceUrl;

    /**
     * Defining zone in which client should fetch for other services
     */
    @ConfigItem(defaultValue = "true")
    boolean preferSameZone;

    /**
     * default page
     */
    @ConfigItem(defaultValue = "/")
    String homePageUrl;

    /**
     * some extra tags which identifies app
     */
    @ConfigItem
    Map<String, String> metadata;

    /**
     * Check the application state
     */
    @ConfigItem(defaultValue = "${quarkus.eureka.heartbeat.status-path:/info/status}")
    String statusPageUrl;

    /**
     * Heartbeats which ensure the application is alive
     */
    @ConfigItem(defaultValue = "${quarkus.eureka.heartbeat.health-path:/info/health}")
    String healthCheckUrl;

    /**
     * Initial delay in seconds for health check before eureka registration
     */
    @ConfigItem(defaultValue = "3")
    long healthCheckInitialDelay;

    public static class NetworkConverter implements Converter<String> {
        private static final long serialVersionUID = -423310887944694372L;

        @Override
        public String convert(final String hostname) {
            if (hostname != null && !hostname.trim().isEmpty()) {
                return hostname;
            }
            return HostNameDiscovery.getHostname();
        }
    }

}
