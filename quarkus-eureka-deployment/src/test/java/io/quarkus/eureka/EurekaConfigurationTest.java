package io.quarkus.eureka;

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
    InstanceInfo instanceInfo;

    @Inject
    ServiceLocationConfig serviceLocationConfig;

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap
                    .create(JavaArchive.class)
                    .addAsResource("eureka-config.properties", "application.properties")
            );

    @Test
    @DisplayName(value = "reading configuration properties for eureka")
    public void shouldLoadEurekaConfigAndRegisterBeans() throws InterruptedException {

        assertThat(instanceInfo)
                .isNotNull()
                .extracting("app", "vipAddress", "secureVipAddress", "status")
                .containsExactly("QUARKUS-EUREKA", "quarkus-eureka", "quarkus-eureka", Status.STARTING);

        assertThat(instanceInfo.getPort()).extracting("port", "enabled").containsExactly("8001", "true");
        assertThat(instanceInfo.getSecurePort()).extracting("port", "enabled").containsExactly("8001", "false");
        assertThat(instanceInfo.getHomePageUrl()).endsWith(":8001/");
        assertThat(instanceInfo.getStatusPageUrl()).endsWith(":8001/info/status");
        assertThat(instanceInfo.getHealthCheckUrl()).endsWith(":8001/info/health");

        assertThat(serviceLocationConfig).isNotNull();
        assertThat(serviceLocationConfig.getLocations())
                .isNotEmpty()
                .hasSize(1)
                .containsExactly("http://localhost:8761/eureka");
    }

}
