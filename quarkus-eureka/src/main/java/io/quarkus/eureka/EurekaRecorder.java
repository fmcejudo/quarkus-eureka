package io.quarkus.eureka;

import io.quarkus.arc.runtime.BeanContainer;
import io.quarkus.eureka.client.InstanceInfo;
import io.quarkus.eureka.config.EurekaConfiguration;
import io.quarkus.eureka.config.ServiceLocationConfig;
import io.quarkus.eureka.operation.OperationFactory;
import io.quarkus.eureka.operation.heartbeat.HeartBeatOperation;
import io.quarkus.eureka.operation.query.MultipleInstanceQueryOperation;
import io.quarkus.eureka.operation.query.SingleInstanceQueryOperation;
import io.quarkus.eureka.operation.register.RegisterOperation;
import io.quarkus.eureka.operation.remove.RemoveInstanceOperation;
import io.quarkus.eureka.registration.EurekaRegistrationService;
import io.quarkus.runtime.annotations.Recorder;
import org.jboss.logging.Logger;

import javax.ws.rs.ProcessingException;

import static io.quarkus.eureka.config.DefaultInstanceInfoContext.withConfiguration;
import static java.util.Arrays.asList;

@Recorder
public class EurekaRecorder {

    private final Logger logger = Logger.getLogger(this.getClass());

    public void registerServiceInEureka(final EurekaConfiguration eurekaConfiguration,
                                        final BeanContainer beanContainer) {
        try {
            logger.info("registering eurekaService");
            InstanceInfo instanceInfo = InstanceInfo.of(withConfiguration(eurekaConfiguration));
            ServiceLocationConfig serviceLocationConfig = new ServiceLocationConfig(eurekaConfiguration);

            OperationFactory operationFactory = createOperationFactory();

            beanContainer.instance(EurekaProducer.class).setOperationFactory(operationFactory);
            beanContainer.instance(EurekaProducer.class).setInstanceInfo(instanceInfo);
            beanContainer.instance(EurekaProducer.class).setServiceLocationConfig(serviceLocationConfig);
            new EurekaRegistrationService(serviceLocationConfig, instanceInfo, operationFactory).register();

        } catch (ProcessingException ex) {
            logger.error("error connecting with eureka registry service", ex.getCause());
        } catch (Exception ex) {
            logger.error(ex);
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
