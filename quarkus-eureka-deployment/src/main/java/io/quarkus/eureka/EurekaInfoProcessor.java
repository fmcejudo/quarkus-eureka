package io.quarkus.eureka;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanContainerBuildItem;
import io.quarkus.arc.deployment.BeanContainerListenerBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.eureka.config.EurekaConfiguration;


public class EurekaInfoProcessor {

    @Record(ExecutionTime.RUNTIME_INIT)
    @BuildStep
    public void eurekaStep(final EurekaInfoRecorder eurekaInfoRecorder) {
        eurekaInfoRecorder.eurekaIsPlaying();
    }

    @Record(ExecutionTime.RUNTIME_INIT)
    @BuildStep
    public void applyConfiguration(final EurekaRecorder eurekaRecorder,
                                   final EurekaConfiguration eurekaConfiguration,
                                   final BeanContainerBuildItem beanContainer) {
        eurekaRecorder.configureProperties(eurekaConfiguration, beanContainer.getValue());
        eurekaRecorder.registerServiceInEureka(beanContainer.getValue());
    }

    @Record(ExecutionTime.STATIC_INIT)
    @BuildStep(providesCapabilities = "io.quarkus.eureka")
    public void stepConfiguration(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer,
                                  BuildProducer<BeanContainerListenerBuildItem> containerListenerProducer,
                                  BuildProducer<FeatureBuildItem> featureProducer) {

        featureProducer.produce(new FeatureBuildItem("eureka"));

        AdditionalBeanBuildItem eurekaBuildItem = AdditionalBeanBuildItem.unremovableOf(EurekaProducer.class);
        additionalBeanProducer.produce(eurekaBuildItem);
    }

}
