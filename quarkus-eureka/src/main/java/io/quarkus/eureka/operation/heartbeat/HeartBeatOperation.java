package io.quarkus.eureka.operation.heartbeat;

import io.quarkus.eureka.client.InstanceInfo;
import io.quarkus.eureka.operation.Operation;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.logging.Logger;

import static io.quarkus.eureka.client.Status.UP;
import static java.lang.String.format;
import static java.util.Collections.singletonMap;

public class HeartBeatOperation implements Operation {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public void heartbeat(final String location, final InstanceInfo instanceInfo) {

        String appId = instanceInfo.getApp();

        logger.info(format("%s heartbeat to %s", appId, location));

        final String path = String.join("/", "apps", appId, Operation.INSTANCE_ID);
        Map<String, InstanceInfo> instance = singletonMap("instance", instanceInfo.withStatus(UP));

        try {
            ResteasyClientBuilder.newClient()
                    .target(String.join("/", location, path))
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .put(Entity.entity(instance, MediaType.APPLICATION_JSON_TYPE))
                    .close();
        } catch (ProcessingException e) {
            logger.info(format("remote endpoint %s does not response", String.join("/", location, path)));
        }
    }
}
