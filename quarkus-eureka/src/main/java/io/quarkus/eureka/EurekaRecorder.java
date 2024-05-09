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

import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.eureka.client.InstanceInfo;
import io.quarkus.eureka.config.EurekaRuntimeConfiguration;
import io.quarkus.eureka.config.ServiceLocationConfig;
import io.quarkus.eureka.operation.OperationFactory;
import io.quarkus.eureka.operation.heartbeat.HeartBeatOperation;
import io.quarkus.eureka.operation.query.MultipleInstanceQueryOperation;
import io.quarkus.eureka.operation.query.SingleInstanceQueryOperation;
import io.quarkus.eureka.operation.register.RegisterOperation;
import io.quarkus.eureka.operation.remove.RemoveInstanceOperation;
import io.quarkus.eureka.registration.EurekaInstancesRegistration;
import io.quarkus.runtime.annotations.Recorder;
import jakarta.ws.rs.ProcessingException;

import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.quarkus.eureka.config.DefaultInstanceInfoContext.withConfiguration;
import static java.util.Arrays.asList;

@Recorder
public class EurekaRecorder {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public void registerServiceInEureka(final EurekaRuntimeConfiguration eurekaRuntimeConfiguration,
                                        final BeanContainer beanContainer) {
        if(!eurekaRuntimeConfiguration.enable) {
            return;
        }
        try {
            logger.info("registering eurekaService");
            InstanceInfo instanceInfo = InstanceInfo
                    .of(withConfiguration(eurekaRuntimeConfiguration));
            ServiceLocationConfig serviceLocationConfig = new ServiceLocationConfig(
                    eurekaRuntimeConfiguration);

            OperationFactory operationFactory = createOperationFactory();

            beanContainer.beanInstance(EurekaProducer.class).setOperationFactory(operationFactory);
            beanContainer.beanInstance(EurekaProducer.class).setInstanceInfo(instanceInfo);
            beanContainer.beanInstance(EurekaProducer.class)
                    .setServiceLocationConfig(serviceLocationConfig);
            EurekaInstancesRegistration.createRegistration(
                    serviceLocationConfig, instanceInfo, operationFactory
            ).register(Executors.newScheduledThreadPool(3));

        } catch (ProcessingException ex) {
            logger.log(Level.SEVERE, "error connecting with eureka registry service", ex);
            throw new RuntimeException(ex);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "error registering eureka", ex);
            throw new RuntimeException(ex);
        }
    }

    private OperationFactory createOperationFactory() {
        RegisterOperation registerOperation = new RegisterOperation();
        HeartBeatOperation heartBeatOperation = new HeartBeatOperation();
        SingleInstanceQueryOperation singleQueryOperation = new SingleInstanceQueryOperation();
        MultipleInstanceQueryOperation multipleQueryOperation = new MultipleInstanceQueryOperation();
        RemoveInstanceOperation removeInstanceOperation = new RemoveInstanceOperation();
        return new OperationFactory(asList(
                registerOperation,
                heartBeatOperation,
                singleQueryOperation,
                multipleQueryOperation,
                removeInstanceOperation
        ));
    }

}
