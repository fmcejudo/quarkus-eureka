package io.quarkus.eureka.heartbeat;

import io.quarkus.test.QuarkusUnitTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

class DefaultHeartBeatTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withConfigurationResource("application-enabled-heartbeat.properties")
            .withEmptyApplication();

    @Test
    public void shouldEnableHealth() {
        RestAssured.when().get("/info/health").then().statusCode(200);
    }

    @Test
    public void shouldEnableStatus() {
        RestAssured.when().get("/info/status").then().statusCode(200);
    }
}

