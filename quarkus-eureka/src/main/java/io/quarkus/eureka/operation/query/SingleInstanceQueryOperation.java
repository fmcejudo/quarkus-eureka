package io.quarkus.eureka.operation.query;

import org.jboss.resteasy.spi.NotImplementedYetException;

/**
 * It retrieves information about the instances which are deployed in the location in which the service registers.
 * <p>
 * appId is the value given to the `app` in the instanceInfo
 * instanceId in datacenters is the hostname, in AWS the instance id of the instance.
 */
public class SingleInstanceQueryOperation extends QueryOperation {

    public InstanceResult findInstance(final String location, final String appId, final String instanceId) {
        throw new NotImplementedYetException("This api is not implemented for Eureka 1.x");
    }

    public InstanceResult findInstanceById(final String location, final String instanceId) {
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
