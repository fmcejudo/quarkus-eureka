package io.quarkus.eureka.util;

import io.quarkus.eureka.config.ServiceLocationConfig;
import io.quarkus.eureka.operation.OperationFactory;
import io.quarkus.eureka.operation.query.ApplicationResult;
import io.quarkus.eureka.operation.query.InstanceResult;
import io.quarkus.eureka.operation.query.MultipleInstanceQueryOperation;

import java.util.stream.Stream;

public class ServiceDiscovery {
    private final ServiceLocationConfig serviceLocationConfig;
    private final OperationFactory operationFactory;

    public ServiceDiscovery(ServiceLocationConfig serviceLocationConfig, OperationFactory operationFactory) {
        this.serviceLocationConfig = serviceLocationConfig;
        this.operationFactory = operationFactory;
    }

    public Stream<String> findServiceLocations(final String appId) {
        return serviceLocationConfig.getLocations()
                .stream()
                .map(location ->
                        operationFactory.get(MultipleInstanceQueryOperation.class)
                                .findInstance(location, appId.toUpperCase())
                )
                .filter(ApplicationResult::success)
                .flatMap(applicationResult ->
                        applicationResult.getInstanceResults().stream().map(InstanceResult::getHomePageUrl)
                );
    }
}
