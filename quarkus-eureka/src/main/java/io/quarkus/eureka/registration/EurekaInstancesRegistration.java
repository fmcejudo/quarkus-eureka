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
import io.quarkus.eureka.config.Location;
import io.quarkus.eureka.config.ServiceLocationConfig;
import io.quarkus.eureka.operation.OperationFactory;
import io.quarkus.eureka.operation.heartbeat.HeartBeatOperation;
import io.quarkus.eureka.operation.query.ApplicationResult;
import io.quarkus.eureka.operation.query.InstanceResult;
import io.quarkus.eureka.operation.query.MultipleInstanceQueryOperation;
import io.quarkus.eureka.operation.register.RegisterOperation;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Logger;

import static io.quarkus.eureka.client.Status.UP;

@FunctionalInterface
public interface EurekaInstancesRegistration {


    public abstract void register(final ScheduledExecutorService scheduledExecutorService);


    public static EurekaInstancesRegistration createRegistration(final ServiceLocationConfig serviceLocationConfig,
                                                                 final InstanceInfo instanceInfo,
                                                                 final OperationFactory operationFactory) {

        var eurekaSingleInstanceRegister = new EurekaSingleInstanceRegister(instanceInfo, operationFactory);

        return (executorService) -> serviceLocationConfig.getLocations().forEach(
                location -> registerInstance(executorService, eurekaSingleInstanceRegister, location));
    }

    private static void registerInstance(final ScheduledExecutorService executorService,
                                         final EurekaSingleInstanceRegister eurekaSingleInstanceRegister,
                                         final Location location) {
        final long initialDelay = 2L;
        final long fixedDelay = 40L;

        executorService.scheduleWithFixedDelay(
                eurekaSingleInstanceRegister.registerLocation(location),
                initialDelay,
                fixedDelay,
                TimeUnit.SECONDS
        );
    }

}

class EurekaSingleInstanceRegister {

    private final OperationFactory operationFactory;
    private final InstanceInfo instanceInfo;
    private final InstanceHealthCheckService instanceHealthCheckService;

    public EurekaSingleInstanceRegister(InstanceInfo instanceInfo, OperationFactory operationFactory) {
        this.instanceInfo = instanceInfo;
        this.operationFactory = operationFactory;
        this.instanceHealthCheckService = new InstanceHealthCheckService();
    }

    Runnable registerLocation(final Location location) {
        return () -> RegistrationFlow.instanceHealthCheck(
                () -> instanceHealthCheckService.healthCheck(instanceInfo.getHealthCheckUrl())
        ).eurekaHealthCheck(() ->
                this.getApplicationStatus(location)
        ).isRegistered(
                queryResponse ->
                        operationFactory.get(HeartBeatOperation.class).heartbeat(location, instanceInfo)
        ).isNotRegistered(
                queryResponse ->
                        operationFactory.get(RegisterOperation.class).register(location, instanceInfo)
        );
    }

    private InstanceResult getApplicationStatus(final Location location) {
        ApplicationResult applicationResult = operationFactory.get(MultipleInstanceQueryOperation.class)
                .findInstance(location, instanceInfo.getApp());

        List<InstanceResult> instanceResults = applicationResult.getInstanceResults();

        return instanceResults.stream()
                .filter(instanceResult -> instanceInfo.getInstanceId().equals(instanceResult.getInstanceId()))
                .findFirst().orElse(InstanceResult.error());
    }

    private record RegistrationFlow(Status status) {

        private static final Logger LOGGER = Logger.getLogger(RegistrationFlow.class.getName());

        private static RegistrationFlow instanceHealthCheck(final Supplier<Status> statusSupplier) {
            try {
                return new RegistrationFlow(statusSupplier.get());
            } catch (Exception e) {
                // We need to log the errors in this thread, as this is inside an Executor which is not bind to
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