package io.quarkus.eureka.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class LocationTest {

    String encodedCredentials;

    @BeforeEach
    void setUp() throws Exception {
        // 
        // user:pass as Base64String
        encodedCredentials = Base64.getEncoder().encodeToString("user:pass".getBytes("UTF-8"));
    }

    @Test
    void shouldParseUrlWithoutBasicAuth() {

        String url = "http://example.de";

        Location l = new Location(url);

        assertFalse(l.hasBasicAuthToken());
    }

    @Test
    void shouldParseUrlWithBasicAuth() {

        String url = "http://user:pass@example.de";

        Location l = new Location(url);

        assertTrue(l.hasBasicAuthToken());
        assertEquals(l.getBasicAuthToken(), encodedCredentials);
    }

    @Test
    void shouldParseUrlWithBasicAuthTLS() {

        String url = "https://user:pass@example.de";

        Location l = new Location(url);

        assertTrue(l.hasBasicAuthToken());
        assertEquals(l.getBasicAuthToken(), encodedCredentials);
    }

    @Test
    void shouldParseUrlWithBasicAuthWithoutHTTP() {

        String url = "user:pass@example.de";

        Location l = new Location(url);

        assertTrue(l.hasBasicAuthToken());
        assertEquals(l.getBasicAuthToken(), encodedCredentials);
    }

    @Test
    void shouldParseUrlWithSubDomain() {

        String url = "test.example.de/eureka";

        Location l = new Location(url);

        assertFalse(l.hasBasicAuthToken());
        assertEquals(l.getBasicAuthToken(), null);
    }

    @Test
    void shouldParseUrlWithBasicAuthWithSubDomain() {

        String url = "http://user:pass@test2.example.de/eureka";

        Location l = new Location(url);

        assertTrue(l.hasBasicAuthToken());
        assertEquals(l.getBasicAuthToken(), encodedCredentials);
    }

    @Test
    void authTokenShoudBeDecodeable() throws Exception {

        String url = "http://user:pass@test2.example.de/eureka";

        Location l = new Location(url);

        assertTrue(l.hasBasicAuthToken());
        assertEquals(l.getBasicAuthToken(), encodedCredentials);

        String tokenDecoded = new String(Base64.getDecoder().decode(l.getBasicAuthToken()), "UTF-8");

        assertEquals(tokenDecoded, "user:pass");
    }
}