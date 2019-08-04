package io.quarkus.eureka;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanContainerListenerBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.eureka.config.Client;
import io.quarkus.eureka.config.EurekaConfiguration;
import io.quarkus.eureka.config.EurekaRecorder;


public class EurekaInfoProcessor {

    EurekaConfiguration eurekaConfiguration;

    @Record(ExecutionTime.RUNTIME_INIT)
    @BuildStep
    public void eurekaStep(final EurekaInfoRecorder eurekaInfoRecorder) {
        eurekaInfoRecorder.eurekaIsPlaying();
    }

    @Record(ExecutionTime.STATIC_INIT)
    @BuildStep
    public void stepConfiguration(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer,
                                  BuildProducer<BeanContainerListenerBuildItem> containerListenerProducer,
                                  final EurekaRecorder eurekaRecorder) {
        System.out.println("client bean is being created...");
        AdditionalBeanBuildItem eurekaBuildItem = AdditionalBeanBuildItem.unremovableOf(Client.class);
        additionalBeanProducer.produce(eurekaBuildItem);

        containerListenerProducer.produce(
                new BeanContainerListenerBuildItem(eurekaRecorder.createEurekaProducer(eurekaConfiguration)));
    }

}
