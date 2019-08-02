package io.quarkus.eureka;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;

public class EurekaStepBuilder {

    @Record(ExecutionTime.RUNTIME_INIT)
    @BuildStep
    public void eurekaStep(final EurekaInfoRecorder eurekaInfoRecorder) {
        eurekaInfoRecorder.eurekaIsPlaying();
    }
}
