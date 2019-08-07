package io.quarkus.eureka.registration;

import io.quarkus.eureka.client.InstanceInfo;
import io.quarkus.eureka.client.Status;
import io.quarkus.eureka.config.ServiceLocationConfig;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static io.quarkus.eureka.client.Status.DOWN;
import static io.quarkus.eureka.client.Status.UNKNOWN;
import static java.lang.String.format;
import static java.util.Collections.singletonMap;
import static javax.ws.rs.core.Response.Status.Family.CLIENT_ERROR;
import static javax.ws.rs.core.Response.Status.Family.SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.Family.SUCCESSFUL;

public class EurekaRegistrationService {

    private final InstanceInfo instanceInfo;

    private final ScheduledExecutorService executorService;

    private final ServiceLocationConfig serviceLocationConfig;

    public EurekaRegistrationService(final ServiceLocationConfig serviceLocationConfig,
                                     final InstanceInfo instanceInfo) {
        this.instanceInfo = instanceInfo;
        this.serviceLocationConfig = serviceLocationConfig;
        this.executorService = Executors.newScheduledThreadPool(3);
    }

    public EurekaRegistrationService(final ServiceLocationConfig serviceLocationConfig,
                                     final InstanceInfo instanceInfo,
                                     final ScheduledExecutorService executorService) {
        this.instanceInfo = instanceInfo;
        this.serviceLocationConfig = serviceLocationConfig;
        this.executorService = executorService;
    }

    public void register() {
        serviceLocationConfig.getLocations()
                .forEach(location -> executorService.scheduleWithFixedDelay(
                        new RegisterService(location, instanceInfo), 2L, 40L, TimeUnit.SECONDS
                ));
    }

    static class RegisterService implements Runnable {

        private Logger logger = Logger.getLogger(this.getClass());
        private final String location;
        private final InstanceInfo instanceInfo;
        private Status lastStatus;

        RegisterService(final String location, final InstanceInfo instanceInfo) {
            this.location = location;
            this.instanceInfo = instanceInfo;
        }

        @Override
        public void run() {
            logger.info("checking heart beat for location " + location);
            Status newStatus = requestHealthCheck(instanceInfo.getHealthCheckUrl());
            if (!newStatus.equals(lastStatus)) {
                logger.info(format("last time had status %s and it became %s", lastStatus, newStatus));
                lastStatus = newStatus;
                updateStatusInEureka(instanceInfo, newStatus);
            }
        }

        private Status requestHealthCheck(final String healthCheckUrl) {
            Response response = ResteasyClientBuilder.newClient()
                    .target(healthCheckUrl)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get();
            return getStatusFromReponse(response);
        }

        private Status getStatusFromReponse(final Response response) {

            if (!response.getStatusInfo().getFamily().equals(SUCCESSFUL)) {
                return DOWN;
            }

            Map<String, String> body = response.readEntity(Map.class);
            return body.entrySet()
                    .stream()
                    .filter(e -> e.getKey().equalsIgnoreCase("status"))
                    .map(Map.Entry::getValue)
                    .map(String::toUpperCase)
                    .map(Status::valueOf)
                    .findFirst().orElse(UNKNOWN);
        }

        private void updateStatusInEureka(final InstanceInfo instanceInfo, final Status newStatus) {
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
                lastStatus = null;
            }
        }
    }

}
