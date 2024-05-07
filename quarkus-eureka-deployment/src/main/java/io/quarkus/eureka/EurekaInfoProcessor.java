/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.quarkus.eureka;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanContainerBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.eureka.config.EurekaBuildTimeConfiguration;
import io.quarkus.eureka.config.EurekaRuntimeConfiguration;
import io.quarkus.eureka.heartbeat.HealthCheckController;
import io.quarkus.eureka.heartbeat.StatusCheckController;
import io.quarkus.undertow.deployment.ServletBuildItem;


public class EurekaInfoProcessor {

    @Record(ExecutionTime.RUNTIME_INIT)
    @BuildStep
    public void applyConfiguration(final EurekaRecorder eurekaRecorder,
        final EurekaRuntimeConfiguration eurekaRuntimeConfiguration,
        final BeanContainerBuildItem beanContainer) {
        eurekaRecorder.registerServiceInEureka(eurekaRuntimeConfiguration, beanContainer.getValue());
    }

    @Record(ExecutionTime.STATIC_INIT)
    @BuildStep
    public AdditionalBeanBuildItem stepConfiguration(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer,
                                       BuildProducer<FeatureBuildItem> featureProducer,
                                       final EurekaRecorder eurekaRecorder) {

        featureProducer.produce(new FeatureBuildItem("eureka"));

        AdditionalBeanBuildItem eurekaBuildItem = AdditionalBeanBuildItem.unremovableOf(EurekaProducer.class);
        additionalBeanProducer.produce(eurekaBuildItem);
        return eurekaBuildItem;
    }

    @BuildStep(onlyIf = IsHealthEnabled.class)
    ServletBuildItem healthCheckBuildItem(final EurekaBuildTimeConfiguration eurekaBuildTimeConfiguration) {

        return ServletBuildItem.builder("quarkus-eureka-health", HealthCheckController.class.getName())
                .addMapping(eurekaBuildTimeConfiguration.heartBeat().healthPath())
                .build();
    }

    @BuildStep(onlyIf = IsHealthEnabled.class)
    ServletBuildItem statusCheckBuildItem(final EurekaBuildTimeConfiguration eurekaBuildTimeConfiguration) {

        return ServletBuildItem.builder("quarkus-eureka-status", StatusCheckController.class.getName())
                .addMapping(eurekaBuildTimeConfiguration.heartBeat().statusPath())
                .build();
    }

}
