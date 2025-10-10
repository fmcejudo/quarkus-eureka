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

import java.util.Map;

import io.quarkus.eureka.util.HostNameDiscovery;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithConverter;
import io.smallrye.config.WithDefault;
import org.eclipse.microprofile.config.spi.Converter;

@ConfigMapping(prefix = "quarkus.eureka")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface EurekaRuntimeConfiguration {

    /**
     * enable the eureka client
     */
    @WithDefault("true")
    boolean enable();

    /**
     * port where eureka server will redirect to attend the service
     */
    @WithDefault("${quarkus.http.port}")
    Integer port();

    /**
     * name of the service as it will turn up in Eureka
     */
    @WithDefault("${quarkus.application.name}")
    String name();

    /**
     * base-path where the application resides
     */
    @WithDefault("${quarkus.http.root-path:/}")
    String contextPath();

    /**
     * Name used by load balancer to redirect to the service
     */
    @WithDefault("${quarkus.application.name}")
    String vipAddress();

    /**
     * The hostname, otherwise it will be guest from OS primitives
     */
    @WithConverter(NetworkConverter.class)
    String hostName();

    /**
     * Determines if the local ip address should be used instead of the hostName.
     */
    @WithDefault("true")
    boolean preferIpAddress();

    /**
     * if AWS environment, in which region this registry service is
     */
    @WithDefault("default")
    String region();

    /**
     * instances of registry services in which the application will publish itself
     */
    Map<String, String> serviceUrl();

    /**
     * Defining zone in which client should fetch for other services
     */
    @WithDefault("true")
    boolean preferSameZone();

    /**
     * default page
     */
    @WithDefault("/")
    String homePageUrl();

    /**
     * some extra tags which identifies app
     */
    Map<String, String> metadata();

    /**
     * Check the application state
     */
    @WithDefault("${quarkus.eureka.heartbeat.status-path:/info/status}")
    String statusPageUrl();

    /**
     * Heartbeats which ensure the application is alive
     */
    @WithDefault("${quarkus.eureka.heartbeat.health-path:/info/health}")
    String healthCheckUrl();

    /**
     * Initial delay in seconds for health check before eureka registration
     */
    @WithDefault("3")
    long healthCheckInitialDelay();

    public static class NetworkConverter implements Converter<String> {

        @Override
        public String convert(final String hostname) {
            if (hostname != null && !hostname.trim().isEmpty()) {
                return hostname;
            }
            return HostNameDiscovery.getHostname();
        }
    }

}
