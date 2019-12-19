package io.quarkus.eureka.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class LocationTest {

    String encodedCredentials;

    @BeforeEach
    void setUp() throws Exception {
        encodedCredentials = Base64.getEncoder().encodeToString("user:pass".getBytes("UTF-8"));
    }

    @Test
    void shouldParseUrlWithoutBasicAuth() {

        String url = "http://example.de";

        Location l = new Location(url);

        assertThat(l.hasBasicAuthToken()).isFalse();
    }

    @Test
    void shouldParseUrlWithBasicAuth() {

        String url = "http://user:pass@example.de";

        Location l = new Location(url);

        assertThat(l.hasBasicAuthToken()).isTrue();
        assertThat(l.getBasicAuthToken()).isEqualTo(encodedCredentials);
    }

    @Test
    void shouldParseUrlWithBasicAuthTLS() {

        String url = "https://user:pass@example.de";

        Location l = new Location(url);

        assertThat(l.hasBasicAuthToken()).isTrue();
        assertThat(l.getBasicAuthToken()).isEqualTo(encodedCredentials);
    }

    @Test
    void shouldParseUrlWithBasicAuthWithoutHTTP() {

        String url = "user:pass@example.de";

        Location l = new Location(url);

        assertThat(l.hasBasicAuthToken()).isTrue();
        assertThat(l.getBasicAuthToken()).isEqualTo(encodedCredentials);
    }

    @Test
    void shouldParseUrlWithSubDomain() {

        String url = "test.example.de/eureka";

        Location l = new Location(url);

        assertThat(l.hasBasicAuthToken()).isFalse();
        assertThat(l.getBasicAuthToken()).isNull();
    }

    @Test
    void shouldParseUrlWithBasicAuthWithSubDomain() {

        String url = "http://user:pass@test2.example.de/eureka";

        Location l = new Location(url);

        assertThat(l.hasBasicAuthToken()).isTrue();
        assertThat(l.getBasicAuthToken()).isEqualTo(encodedCredentials);
    }

    @Test
    void authTokenShoudBeDecodeable() throws Exception {

        String url = "http://user:pass@test2.example.de/eureka";

        Location l = new Location(url);

        assertThat(l.hasBasicAuthToken()).isTrue();
        assertThat(l.getBasicAuthToken()).isEqualTo(encodedCredentials);

        String tokenDecoded = new String(Base64.getDecoder().decode(l.getBasicAuthToken()), "UTF-8");

        assertThat(tokenDecoded).isEqualTo("user:pass");
    }
}