package io.quarkus.eureka.operation.remove;

import io.quarkus.eureka.operation.Operation;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;

import static io.quarkus.eureka.util.HostNameDiscovery.getHostname;
import static java.lang.String.format;

public class RemoveInstanceOperation implements Operation {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private static final String INSTANCE_ID;

    static {
        INSTANCE_ID = getHostname();
    }

    public void remove(final String location, final String appId) {

        logger.info(format("Deregistering %s from %s", appId, location));

        final String path = String.join("/", "apps", appId, INSTANCE_ID);
        try {
            ResteasyClientBuilder.newClient()
                    .target(String.join("/", location, path))
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .delete()
                    .close();
        } catch (ProcessingException e) {
            logger.info(format("remote endpoint %s does not response", String.join("/", location, path)));
        }
    }
}
