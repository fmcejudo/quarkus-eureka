package io.quarkus.eureka.util;

import java.util.List;

import javax.inject.Inject;

import io.quarkus.eureka.client.EurekaClient;
import io.quarkus.eureka.config.ServiceLocationConfig;
import io.quarkus.test.QuarkusUnitTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

class ServiceDiscoveryTest {

    @Inject
    EurekaClient eurekaClient;

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
        .setArchiveProducer(() -> ShrinkWrap
            .create(JavaArchive.class)
            .addAsResource("eureka-default-config.properties", "application.properties")
        );

    @Test
    void shouldDiscoverServiceUrls() {
        //Given


        //When

        //Then

    }

}