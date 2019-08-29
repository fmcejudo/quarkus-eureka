package io.quarkus.eureka.operation.query;

import java.util.Optional;

/**
 * It retrieves information about the instances which are deployed in the location in which the service registers.
 * <p>
 * appId is the value given to the `app` in the instanceInfo
 * instanceId in datacenters is the hostname, in AWS the instance id of the instance.
 */
public class SingleInstanceQueryOperation implements QueryOperation {

    public InstanceResult findInstance(final String location, final String appId, final String instanceId) {
        final String path = String.join("/", "apps", appId, instanceId);
        return queryInstance(location, path);
    }

    public InstanceResult findInstanceById(final String location, final String instanceId) {
        final String path = String.join("/", "instances", instanceId);
        return queryInstance(location, path);
    }

    private InstanceResult queryInstance(String location, String path) {
        return Optional.ofNullable(query(location, path, InstanceResult.class))
                .orElseGet(InstanceResult::error);
    }


}
