package io.quarkus.eureka;

import io.quarkus.eureka.config.EurekaConfiguration;
import io.quarkus.test.QuarkusUnitTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.inject.Inject;

class EurekaConfigTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap
                    .create(JavaArchive.class)
                    .addAsResource("application.properties")
            );

    @Inject
    EurekaConfiguration eurekaConfiguration;

    @Test
    void shouldFindEurekaConfig() {
        Assertions.assertNotNull(eurekaConfiguration);
    }


}
