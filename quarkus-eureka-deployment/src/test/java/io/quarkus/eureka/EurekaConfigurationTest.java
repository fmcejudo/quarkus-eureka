package io.quarkus.eureka;

import io.quarkus.eureka.config.UrlEurekaClient;
import io.quarkus.test.Mock;
import io.quarkus.test.QuarkusUnitTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.inject.Inject;

public class EurekaConfigurationTest {

    @Inject
    UrlEurekaClient urlEurekaClient;

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap
                    .create(JavaArchive.class)
                    .addAsResource("eureka-config.properties","application.properties")
            );

    @Test
    @DisplayName(value = "reading configuration properties for eureka")
    public void shouldFindEurekaConfig() {
        Assertions.assertNotNull(urlEurekaClient);
        //Assertions.assertEquals("quarkus-eureka", urlEurekaClient);
        //Assertions.assertEquals(8001, urlEurekaClient.getPort());
    }

}
