package io.quarkus.eureka.config;

import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class EurekaRecorder {

    public void configureProperties(final EurekaConfiguration eurekaConfiguration, final BeanContainer container) {
        container.instance(EurekaProducer.class).setConfiguration(eurekaConfiguration);
    }
}
