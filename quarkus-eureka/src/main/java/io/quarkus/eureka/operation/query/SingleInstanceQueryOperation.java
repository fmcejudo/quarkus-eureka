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

package io.quarkus.eureka.operation.query;

import org.jboss.resteasy.spi.NotImplementedYetException;

import io.quarkus.eureka.config.Location;

/**
 * It retrieves information about the instances which are deployed in the location in which the service registers.
 * <p>
 * appId is the value given to the `app` in the instanceInfo
 * instanceId in datacenters is the hostname, in AWS the instance id of the instance.
 */
public class SingleInstanceQueryOperation extends QueryOperation {

    public InstanceResult findInstance(final Location location, final String appId, final String instanceId) {
        throw new NotImplementedYetException("This api is not implemented for Eureka 1.x");
    }

    public InstanceResult findInstanceById(final Location location, final String instanceId) {
        throw new NotImplementedYetException("This api is not implemented for Eureka 1.x");
    }

    @Override
    <T> T onNotFound(Class<T> clazz) {
        return (T) InstanceResult.error();
    }

    @Override
    <T> void onError(Class<T> clazz) {

    }
}
