package io.quarkus.eureka.operation.remove;

import io.quarkus.eureka.operation.Operation;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;

import static java.lang.String.format;

public class RemoveInstanceOperation implements Operation {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public void remove(final String location, final String appId) {
        logger.info(format("Deregistering %s from %s", appId, location));
        final String path = String.join("/", "apps", appId, Operation.INSTANCE_ID);
        Client client = ResteasyClientBuilder.newClient();

        try {
            client.target(String.join("/", location, path))
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .delete()
                    .close();
        } catch (ProcessingException e) {
            logger.info(format("remote endpoint %s does not response", String.join("/", location, path)));
        } finally {
            client.close();
        }
    }
}
