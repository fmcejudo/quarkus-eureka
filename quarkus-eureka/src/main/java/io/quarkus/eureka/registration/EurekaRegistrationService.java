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

package io.quarkus.eureka.registration;

import io.quarkus.eureka.client.InstanceInfo;
import io.quarkus.eureka.client.Status;
import io.quarkus.eureka.config.ServiceLocationConfig;
import io.quarkus.eureka.operation.OperationFactory;
import io.quarkus.eureka.operation.heartbeat.HeartBeatOperation;
import io.quarkus.eureka.operation.query.InstanceResult;
import io.quarkus.eureka.operation.query.MultipleInstanceQueryOperation;
import io.quarkus.eureka.operation.register.RegisterOperation;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Logger;

import static io.quarkus.eureka.client.Status.UP;

public class EurekaRegistrationService {

    private final InstanceInfo instanceInfo;

    private final ScheduledExecutorService executorService;

    private final ServiceLocationConfig serviceLocationConfig;

    private final OperationFactory operationFactory;

    private final InstanceHealthCheckService instanceHealthCheckService;

    public EurekaRegistrationService(final ServiceLocationConfig serviceLocationConfig,
                                     final InstanceInfo instanceInfo,
                                     final OperationFactory operationFactory) {
        this(serviceLocationConfig, instanceInfo, operationFactory, Executors.newScheduledThreadPool(3));
    }

    public EurekaRegistrationService(final ServiceLocationConfig serviceLocationConfig,
                                     final InstanceInfo instanceInfo,
                                     final OperationFactory operationFactory,
                                     final ScheduledExecutorService executorService) {
        this.instanceInfo = instanceInfo;
        this.serviceLocationConfig = serviceLocationConfig;
        this.executorService = executorService;
        this.operationFactory = operationFactory;
        this.instanceHealthCheckService = new InstanceHealthCheckService();
    }

    public void register() {

        //TODO shall I first check the application has the defined endpoint up and running before
        // registering with failing parameters

        serviceLocationConfig.getLocations()
                .forEach(location -> executorService.scheduleWithFixedDelay(() -> {

                    RegistrationFlow.instanceHealthCheck(
                            () -> instanceHealthCheckService.healthCheck(instanceInfo.getHealthCheckUrl())
                    ).eurekaHealthCheck(
                            () -> operationFactory.get(MultipleInstanceQueryOperation.class)
                                    .findInstance(location, instanceInfo.getApp())
                                    .getInstanceResults().stream().findFirst().orElse(InstanceResult.error())
                    ).isRegistered(
                            queryResponse ->
                                    operationFactory.get(HeartBeatOperation.class).heartbeat(location, instanceInfo)
                    ).isNotRegistered(
                            queryResponse ->
                                    operationFactory.get(RegisterOperation.class).register(location, instanceInfo)
                    );

                }, 2L, 40L, TimeUnit.SECONDS));
    }

    private static class RegistrationFlow {

        private static final Logger LOGGER = Logger.getLogger(RegistrationFlow.class.getName());

        private Status status;

        private RegistrationFlow(final Status status) {
            this.status = status;
        }

        private static RegistrationFlow instanceHealthCheck(final Supplier<Status> statusSupplier) {
            try {
                return new RegistrationFlow(statusSupplier.get());
            } catch (Exception e) {
                // We need to log the errors in this thread, as this is inside of an Executor which is not bind to
                // the main thread
                LOGGER.warning(e.getMessage());
                throw e;
            }
        }

        private InstanceResult eurekaHealthCheck(final Supplier<InstanceResult> eurekaHealthSupplier) {
            if (UP.equals(this.status)) {
                return eurekaHealthSupplier.get();
            }
            return InstanceResult.error();
        }

    }

}
