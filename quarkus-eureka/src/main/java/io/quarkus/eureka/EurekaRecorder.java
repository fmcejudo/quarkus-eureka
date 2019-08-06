package io.quarkus.eureka;

import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.eureka.config.EurekaConfiguration;
import io.quarkus.eureka.registration.EurekaRegistrationService;
import io.quarkus.runtime.annotations.Recorder;
import org.jboss.logging.Logger;

import javax.ws.rs.ProcessingException;


@Recorder
public class EurekaRecorder {

    private final Logger logger = Logger.getLogger(this.getClass());

    public void configureProperties(final EurekaConfiguration eurekaConfiguration, final BeanContainer container) {
        container.instance(EurekaProducer.class).setConfiguration(eurekaConfiguration);
    }

    public void registerServiceInEureka(final BeanContainer beanContainer) {
        try {
            logger.info("registering eurekaService");
            EurekaProducer eurekaProducer = beanContainer.instance(EurekaProducer.class);
            new EurekaRegistrationService(
                    eurekaProducer.serviceLocationConfig(), eurekaProducer.instanceInfo()
            ).register();

        } catch (ProcessingException ex) {
            logger.error("error connecting with eureka registry service", ex.getCause());
        }
    }
}
