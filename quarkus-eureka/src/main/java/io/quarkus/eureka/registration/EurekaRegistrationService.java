package io.quarkus.eureka.registration;

import io.quarkus.eureka.client.InstanceInfo;
import io.quarkus.eureka.client.Status;
import io.quarkus.eureka.config.ServiceLocationConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.jboss.logging.Logger;

import javax.json.bind.JsonbBuilder;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static io.quarkus.eureka.client.Status.DOWN;
import static io.quarkus.eureka.client.Status.UNKNOWN;
import static io.quarkus.eureka.client.Status.UP;
import static java.lang.String.format;

public class EurekaRegistrationService {


    private final InstanceInfo instanceInfo;

    private final ServiceLocationConfig serviceLocationConfig;

    public EurekaRegistrationService(final ServiceLocationConfig serviceLocationConfig,
                                     final InstanceInfo instanceInfo) {
        this.instanceInfo = instanceInfo;
        this.serviceLocationConfig = serviceLocationConfig;
    }

    public void register() {
        serviceLocationConfig.getLocations().forEach(location -> this.register(location, instanceInfo));
    }

    private void register(final String location, final InstanceInfo instanceInfo) {
        new HeartBeat(instanceInfo).addRegisterService(new RegisterService(location));
    }

    static class HeartBeat implements Runnable {

        private final InstanceInfo instanceInfo;

        private PropertyChangeSupport propertyChangeSupport;

        private HeartBeat(InstanceInfo instanceInfo) {
            this.instanceInfo = instanceInfo;
            this.propertyChangeSupport = new PropertyChangeSupport(this);
        }

        void addRegisterService(RegisterService registerService) {
            propertyChangeSupport.addPropertyChangeListener(registerService);
            checkHealthy();
        }

        private void checkHealthy() {
            Thread thread = new Thread(this);
            thread.start();
        }

        @Override
        public void run() {
            while (true) {
                propertyChangeSupport.firePropertyChange("checkHealth", null, instanceInfo);
                try {
                    TimeUnit.SECONDS.sleep(40L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    static class RegisterService implements PropertyChangeListener {

        private Logger logger = Logger.getLogger(this.getClass());
        private final String location;
        private Status lastStatus;

        RegisterService(final String location) {
            this.location = location;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            InstanceInfo instanceInfo = (InstanceInfo) evt.getNewValue();
            Response response = ClientBuilder.newClient()
                    .target(instanceInfo.getHealthCheckUrl())
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            Status newStatus = getStatusFromReponse(response);
            if (!newStatus.equals(lastStatus)) {
                logger.info(format("last time had status %s and it became %s", lastStatus, newStatus));
                lastStatus = newStatus;
                updateStatusInEureka(instanceInfo, newStatus);
            }
        }

        private Status getStatusFromReponse(Response response) {
            if (response.getStatus() < 300) {
                Map<String, String> body = response.readEntity(Map.class);
                String status = body.entrySet()
                        .stream()
                        .filter(e -> e.getKey().equalsIgnoreCase("status"))
                        .map(Map.Entry::getValue)
                        .findFirst().orElse(null);

                if ("UP".equalsIgnoreCase(status) && !UP.equals(lastStatus)) {
                    return UP;
                } else if ("DOWN".equalsIgnoreCase(status) && !DOWN.equals(lastStatus)) {
                    return DOWN;
                } else if (!UNKNOWN.equals(lastStatus)) {
                    return UNKNOWN;
                }
            } else {
                return DOWN;
            }
            return lastStatus;
        }

        private void updateStatusInEureka(final InstanceInfo instanceInfo, final Status newStatus) {
            instanceInfo.withStatus(newStatus);
            try {
                String registrationUrl = location.concat("/apps/").concat(instanceInfo.getApp());

                Map<String, InstanceInfo> instance = Collections.singletonMap("instance", instanceInfo);
                logger.info(JsonbBuilder.create().toJson(instance));

                Response response = ClientBuilder.newClient()
                        .register(new LoggingFilter())
                        .target(registrationUrl)
                        .request(MediaType.APPLICATION_JSON)
                        .post(Entity.json(instance));

                logger.info(format(
                        "registering the application in Eureka: %d, %s",
                        response.getStatus(),
                        response.readEntity(String.class))
                );
            } catch (ProcessingException ex) {
                logger.info("eureka service is down and no status can be register");
                lastStatus = null;
            }
        }

    }

}
