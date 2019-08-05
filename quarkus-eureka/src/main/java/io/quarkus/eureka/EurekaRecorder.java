package io.quarkus.eureka;

import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.eureka.config.EurekaConfiguration;
import io.quarkus.eureka.config.UrlEurekaClient;
import io.quarkus.runtime.annotations.Recorder;
import org.jboss.logging.Logger;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;


@Recorder
public class EurekaRecorder {

    private final Logger logger = Logger.getLogger(this.getClass());

    public void configureProperties(final EurekaConfiguration eurekaConfiguration, final BeanContainer container) {
        container.instance(EurekaProducer.class).setConfiguration(eurekaConfiguration);
    }

    public void registerServiceInEureka(final EurekaConfiguration eurekaConfiguration) {
        try {
            UrlEurekaClient urlEurekaClient = new UrlEurekaClient(eurekaConfiguration);
            Response responseRegister = urlEurekaClient.register();
            logger.info(urlEurekaClient.getInfo());
            logger.info(String.format("response with status %d", responseRegister.getStatus()));
        } catch (ProcessingException ex) {
            logger.error("error connecting with eureka registry service", ex.getCause());
        }
    }
}
