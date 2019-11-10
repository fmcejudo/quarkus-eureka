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

import io.quarkus.eureka.config.ServiceLocationConfig;
import io.quarkus.eureka.exception.EurekaServiceNotFoundException;
import io.quarkus.eureka.operation.OperationFactory;
import io.quarkus.eureka.operation.query.ApplicationResult;
import io.quarkus.eureka.operation.query.InstanceResult;
import io.quarkus.eureka.operation.query.MultipleInstanceQueryOperation;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.ws.rs.client.WebTarget;

public class EurekaClient {

    private final ServiceLocationConfig serviceLocationConfig;
    private final OperationFactory operationFactory;

    public EurekaClient(final ServiceLocationConfig serviceLocationConfig,
                        final OperationFactory operationFactory) {
        this.serviceLocationConfig = serviceLocationConfig;
        this.operationFactory = operationFactory;
    }

    public WebTarget app(final String appId) {
        String target = serviceLocationConfig.getLocations()
                .stream()
                .map(location ->
                        operationFactory.get(MultipleInstanceQueryOperation.class)
                                .findInstance(location, appId.toUpperCase())
                )
                .filter(ApplicationResult::success)
                .flatMap(applicationResult ->
                        applicationResult.getInstanceResults().stream().map(InstanceResult::getHomePageUrl)
                )
                .findAny()
                .orElseThrow(() -> new EurekaServiceNotFoundException(appId.toUpperCase()));

        return ResteasyClientBuilder.newClient().target(target);
    }

}
