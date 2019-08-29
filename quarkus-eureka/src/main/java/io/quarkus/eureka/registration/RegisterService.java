package io.quarkus.eureka.registration;

import io.quarkus.eureka.client.InstanceInfo;
import io.quarkus.eureka.client.Status;
import io.quarkus.eureka.operation.OperationFactory;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Collections.singletonMap;
import static javax.ws.rs.core.Response.Status.Family.CLIENT_ERROR;
import static javax.ws.rs.core.Response.Status.Family.SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.Family.SUCCESSFUL;

class RegisterService {

    private Logger logger = Logger.getLogger(this.getClass());
    private final String location;
    private final InstanceInfo instanceInfo;

    RegisterService(final String location, final InstanceInfo instanceInfo) {
        this.location = location;
        this.instanceInfo = instanceInfo;
    }

    public void register(final Status newStatus) {
        try {
            String registrationUrl = location.concat("/apps/").concat(instanceInfo.getApp());

            Map<String, InstanceInfo> instance = singletonMap("instance", instanceInfo.withStatus(newStatus));
            Response response = ResteasyClientBuilder.newClient()
                    .target(registrationUrl)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.json(instance));
            if (response.getStatusInfo().getFamily().equals(SUCCESSFUL)) {
                logger.info(format("Service has been registered in %s", location));
            } else if (response.getStatusInfo().getFamily().equals(CLIENT_ERROR)) {
                logger.info(format("Service has problems to register in %s", location));
            } else if (response.getStatusInfo().getFamily().equals(SERVER_ERROR)) {
                logger.info(format("%s returns error message %s", location, response.readEntity(String.class)));
            }
        } catch (ProcessingException ex) {
            logger.info("eureka service is down and no status can be register");
        }
    }
}