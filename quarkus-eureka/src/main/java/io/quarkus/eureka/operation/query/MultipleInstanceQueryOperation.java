package io.quarkus.eureka.operation.query;

import java.util.Optional;

public class MultipleInstanceQueryOperation implements QueryOperation {

    public ApplicationsResult findAllInstances(final String location) {
        final String path = "apps";
        return query(location, path, ApplicationsResult.class);
    }

    public ApplicationResult findInstance(final String location, final String appId) {
        final String path = String.join("/", "apps", appId);
        return query(location, path, ApplicationResult.class);
    }

}
