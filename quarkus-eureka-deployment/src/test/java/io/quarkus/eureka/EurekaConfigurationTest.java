package io.quarkus.eureka;

import io.quarkus.eureka.client.EurekaClient;
import io.quarkus.eureka.client.InstanceInfo;
import io.quarkus.eureka.client.Status;
import io.quarkus.eureka.config.ServiceLocationConfig;
import io.quarkus.test.QuarkusUnitTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

public class EurekaConfigurationTest {

    @Inject
    public EurekaClient eurekaClient;

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap
                    .create(JavaArchive.class)
                    .addAsResource("eureka-config.properties", "application.properties")
            );

    @Test
    @DisplayName(value = "reading configuration properties for eureka")
    public void shouldLoadEurekaConfigAndRegisterBeans() throws InterruptedException {
        assertThat(eurekaClient).isNotNull();
    }

}
