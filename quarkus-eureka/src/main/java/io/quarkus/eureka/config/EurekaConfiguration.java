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

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

import java.util.Map;

@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public class EurekaConfiguration {

    /**
     * port where eureka server will redirect to attend the service
     */
    @ConfigItem
    Integer port;

    /**
     * name of the service as it will turn up in Eureka
     */
    @ConfigItem
    String name;

    /**
     * Name used by load balancer to redirect to the service
     */
    @ConfigItem
    String vipAddress;

    /**
     * The hostname, otherwise it will be guest from OS primitives
     */
    @ConfigItem
    String hostName;

    /**
     * if AWS environment, in which region this registry service is
     */
    @ConfigItem
    String region;

    /**
     * instances of registry services in which the application will publish itself
     * its possible to add basic auth credentials in this url like
     * http://user:password@eureka:8761
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
     * Check the application state
     */
    @ConfigItem(defaultValue = "/info/status")
    String statusPageUrl;

    /**
     * Heartbeats which ensure the application is alive
     */
    @ConfigItem(defaultValue = "/info/health")
    String healthCheckUrl;
}
