package io.quarkus.eureka.heartbeat;

import java.util.Map;

import io.quarkus.eureka.heartbeat.CustomHeartBeatTest.CustomHeartBeatTestProfile;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;


@QuarkusTest
@TestProfile(CustomHeartBeatTestProfile.class)
class CustomHeartBeatTest {

    @Test
    void shouldEnableHealth() {
        RestAssured.when().get("/checks/healthcheck").then().statusCode(200);
    }

    @Test
    void shouldEnableStatus() {
        RestAssured.when().get("/checks/status-check").then().statusCode(200);
    }

    public static class CustomHeartBeatTestProfile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of("quarkus.config.locations", "application-enabled-custom-heartbeat.properties");
        }
    }

}

