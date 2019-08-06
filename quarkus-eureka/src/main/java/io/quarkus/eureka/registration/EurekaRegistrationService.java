package io.quarkus.eureka.registration;

import io.quarkus.eureka.client.InstanceInfo;
import io.quarkus.eureka.config.ServiceLocationConfig;
import org.jboss.logging.Logger;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class EurekaRegistrationService {

    // TODO: This instanceInfo means to check the health of the service to update the registration status
    private final InstanceInfo instanceInfo;
    // TODO: It needs register the application in every location
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
            if (response.getStatus() < 300) {
                logger.info(String.format("%d: %s", response.getStatus(), response.getEntity().toString()));
            } else {
                logger.error(String.format("error: %d", response.getStatus()));
            }

        }
    }

}
