package io.quarkus.eureka.operation;

import static io.quarkus.eureka.util.HostNameDiscovery.getHostname;

public interface Operation {

    String INSTANCE_ID = getHostname();
}
