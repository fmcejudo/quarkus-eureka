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

import java.util.ArrayList;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanContainerBuildItem;
import io.quarkus.arc.deployment.BeanContainerListenerBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.eureka.client.DataCenterInfo;
import io.quarkus.eureka.client.InstanceInfo;
import io.quarkus.eureka.client.PortEnableInfo;
import io.quarkus.eureka.config.EurekaRuntimeConfiguration;


public class EurekaInfoProcessor {

    @Record(ExecutionTime.RUNTIME_INIT)
    @BuildStep
    public void eurekaStep(final EurekaInfoRecorder eurekaInfoRecorder) {
        eurekaInfoRecorder.eurekaIsPlaying();
    }

    @Record(ExecutionTime.RUNTIME_INIT)
    @BuildStep
    public void applyConfiguration(final EurekaRecorder eurekaRecorder,
        final EurekaRuntimeConfiguration eurekaRuntimeConfiguration,
        final BeanContainerBuildItem beanContainer) {
        eurekaRecorder.registerServiceInEureka(eurekaRuntimeConfiguration, beanContainer.getValue());
    }

    @Record(ExecutionTime.STATIC_INIT)
    @BuildStep
    public void stepConfiguration(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer,
        BuildProducer<BeanContainerListenerBuildItem> containerListenerProducer,
        BuildProducer<FeatureBuildItem> featureProducer,
        final EurekaRecorder eurekaRecorder) {

        featureProducer.produce(new FeatureBuildItem("eureka"));

        AdditionalBeanBuildItem eurekaBuildItem = AdditionalBeanBuildItem.unremovableOf(EurekaProducer.class);
        additionalBeanProducer.produce(eurekaBuildItem);
    }

    @BuildStep
    public ReflectiveClassBuildItem registerForReflection() {
        ArrayList<String> dtos = new ArrayList<>();
        dtos.add(InstanceInfo.class.getName());
        dtos.add(DataCenterInfo.class.getName());
        dtos.add(PortEnableInfo.class.getName());
        return new ReflectiveClassBuildItem(true, true, dtos.toArray(new String[dtos.size()]));
    }

}
