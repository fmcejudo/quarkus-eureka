package io.quarkus.eureka.heartbeat;


import java.util.Map;

import io.quarkus.eureka.heartbeat.DefaultHeartBeatTest.HeartBeatTestProfile;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestProfile(HeartBeatTestProfile.class)
class DefaultHeartBeatTest {


    @Test
    public void shouldEnableHealth() {
        RestAssured.when().get("/info/health").then().statusCode(200);
    }

    @Test
    public void shouldEnableStatus() {
        RestAssured.when().get("/info/status").then().statusCode(200);
    }

    public static class HeartBeatTestProfile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                "quarkus.eureka.heartbeat.enabled", "true",
                "quarkus.eureka.heartbeat.health-path", "/info/health",
                "quarkus.eureka.heartbeat.status-path", "/info/status"
            );
        }
    }
}
