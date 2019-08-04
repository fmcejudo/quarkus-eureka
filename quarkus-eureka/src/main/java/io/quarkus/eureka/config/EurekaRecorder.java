package io.quarkus.eureka.config;

import io.quarkus.arc.runtime.BeanContainerListener;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class EurekaRecorder {

    public BeanContainerListener createEurekaProducer(final EurekaConfiguration eurekaConfiguration) {
        return beanContainer -> {
            EurekaProducer eurekaProducer = new EurekaProducer();
            eurekaProducer.setConfiguration(eurekaConfiguration);
        };
    }
}
