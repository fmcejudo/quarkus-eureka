package io.quarkus.eureka.operation;

import static io.quarkus.eureka.util.HostNameDiscovery.getInstanceId;

public interface Operation {

    String INSTANCE_ID = getInstanceId();
}
