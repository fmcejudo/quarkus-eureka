package io.quarkus.eureka.client;

import io.quarkus.eureka.config.ServiceLocationConfig;
import io.quarkus.eureka.exception.EurekaServiceNotFoundException;
import io.quarkus.eureka.operation.OperationFactory;
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
                        operationFactory.get(MultipleInstanceQueryOperation.class).findInstance(location, appId)
                )
                .flatMap(applicationResult ->
                        applicationResult.getInstanceResults().stream().map(InstanceResult::getHomePageUrl)
                )
                .findAny()
                .orElseThrow(() -> new EurekaServiceNotFoundException(appId));

        return ResteasyClientBuilder.newClient().target(target);
    }

}
