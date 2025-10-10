package io.quarkus.eureka.heartbeat;

import java.util.Map;

import io.quarkus.eureka.heartbeat.DisabledHeartBeatTest.DisabledHeartBeatTestProfile;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestProfile(DisabledHeartBeatTestProfile.class)
class DisabledHeartBeatTest {


    @Test
    void shouldNotEnableHealthCheck() {
        RestAssured.when().get("/info/health").then().statusCode(404);
    }

    @Test
    void shouldNotEnableStatusCheck() {
        RestAssured.when().get("/info/status").then().statusCode(404);
    }

    public static class DisabledHeartBeatTestProfile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of("quarkus.config.locations", "application-disabled-heartbeat.properties");
        }
    }
}
