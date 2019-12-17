package io.quarkus.eureka.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.ws.rs.client.Invocation.Builder;

public class AuthHelper {

    /**
     * Reads basic auth params from the location and adds them as auth header.
     * http://user:pass@example.de/....
     * 
     * @param builder
     * @param location
     * @return the builder with
     */
    public static Builder addAuthHeader(Builder builder, String location) {

        if (location.contains("@")) {
            // extract the username and password part
            String[] urlParts = location
                .replace("http://", "")
                .replace("https://", "")
                .split("@");

            if (urlParts.length == 2 && urlParts[0].contains(":")) {
                String authToken = Base64.getEncoder().encodeToString(urlParts[0].getBytes(StandardCharsets.UTF_8));
                builder.header("Authorization", "Basic " + authToken);
            }
        }
        return builder;
    }
}
