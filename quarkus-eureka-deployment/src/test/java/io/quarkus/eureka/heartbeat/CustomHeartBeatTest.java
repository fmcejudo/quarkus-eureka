package io.quarkus.eureka.heartbeat;

import io.quarkus.test.QuarkusUnitTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

class CustomHeartBeatTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withConfigurationResource("application-enabled-custom-heartbeat.properties")
            .withEmptyApplication();

    @Test
    public void shouldEnableHealth() {
        RestAssured.when().get("/checks/healthcheck").then().statusCode(200);
    }

    @Test
    public void shouldEnableStatus() {
        RestAssured.when().get("/checks/status-check").then().statusCode(200);
    }
}

