package io.quarkus.eureka.heartbeat;

import io.quarkus.test.QuarkusUnitTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

class DisabledHeartBeatTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withConfigurationResource("application-disabled-heartbeat.properties")
            .withEmptyApplication();

    @Test
    public void shouldNotEnableHealthCheck() {
        RestAssured.when().get("/info/health").then().statusCode(404);
    }

    @Test
    public void shouldNotEnableStatusCheck() {
        RestAssured.when().get("/info/status").then().statusCode(404);
    }
}
