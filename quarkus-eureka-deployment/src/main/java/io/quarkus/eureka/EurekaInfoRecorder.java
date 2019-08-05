package io.quarkus.eureka;

import io.quarkus.runtime.annotations.Recorder;
import org.jboss.logging.Logger;

@Recorder
public class EurekaInfoRecorder {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    void eurekaIsPlaying() {
        logger.info("Eureka extension is active and it is initialising....");
    }

}
