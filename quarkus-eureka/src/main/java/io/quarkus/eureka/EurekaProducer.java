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

package io.quarkus.eureka;

import io.quarkus.eureka.client.EurekaClient;
import io.quarkus.eureka.client.InstanceInfo;
import io.quarkus.eureka.client.loadBalancer.LoadBalanced;
import io.quarkus.eureka.client.loadBalancer.LoadBalancer;
import io.quarkus.eureka.client.loadBalancer.LoadBalancerType;
import io.quarkus.eureka.client.loadBalancer.Random;
import io.quarkus.eureka.client.loadBalancer.RoundRobin;
import io.quarkus.eureka.config.ServiceLocationConfig;
import io.quarkus.eureka.operation.OperationFactory;
import io.quarkus.eureka.operation.remove.RemoveInstanceOperation;
import io.quarkus.eureka.util.ServiceDiscovery;
import io.quarkus.runtime.ShutdownEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import java.util.logging.Logger;

@ApplicationScoped
public class EurekaProducer {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private OperationFactory operationFactory;

    private InstanceInfo instanceInfo;

    private ServiceLocationConfig serviceLocationConfig;

    @Produces
    @LoadBalanced
    EurekaClient eurekaClient(InjectionPoint ip) {
        LoadBalancer loadBalancer;
        ServiceDiscovery serviceDiscovery = new ServiceDiscovery(serviceLocationConfig, operationFactory);
        if(ip.getAnnotated().getAnnotation(LoadBalanced.class).type() == LoadBalancerType.RANDOM)
            loadBalancer = new Random(serviceDiscovery);
        else if(ip.getAnnotated().getAnnotation(LoadBalanced.class).type() == LoadBalancerType.ROUND_ROBIN)
            loadBalancer = new RoundRobin(serviceDiscovery);
        else
            loadBalancer = new Random(serviceDiscovery);
        return new EurekaClient(loadBalancer);
    }

    @Produces
    EurekaClient eurekaClient() {
        ServiceDiscovery serviceDiscovery = new ServiceDiscovery(serviceLocationConfig, operationFactory);
        return new EurekaClient( new Random(serviceDiscovery));
    }

    void onApplicationStop(@Observes ShutdownEvent shutdownEvent) {
        logger.info("application finished... now we have to deregister from Eureka...");
        String appId = instanceInfo.getApp();
        serviceLocationConfig.getLocations()
                .forEach(location -> operationFactory.get(RemoveInstanceOperation.class).remove(location, appId));
    }

    void setInstanceInfo(final InstanceInfo instanceInfo) {
        this.instanceInfo = instanceInfo;
    }

    void setServiceLocationConfig(final ServiceLocationConfig serviceLocationConfig) {
        this.serviceLocationConfig = serviceLocationConfig;
    }

    void setOperationFactory(final OperationFactory operationFactory) {
        this.operationFactory = operationFactory;
    }
}
