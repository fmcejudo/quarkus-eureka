package io.quarkus.eureka.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Location {

    private String url = "";

    private String basicAuthToken = null;

    public Location() {
	}


	public Location(final String url) {
        this();
        this.url = url;

         // parse for username and password  
        final String[] urlParts = url
        .replace("http://", "")
        .replace("https://", "")
        .split("@");
        if (urlParts.length == 2 && urlParts[0].contains(":")) {
            basicAuthToken = Base64.getEncoder().encodeToString(urlParts[0].getBytes(StandardCharsets.UTF_8));
        }
	}

	public String getUrl() {
		return url;
    }
    
    public boolean hasBasicAuthToken() {
        return basicAuthToken != null;
    }

    public String getBasicAuthToken() {
        return basicAuthToken;
    }

    @Override
    public String toString() {
        return url;
    }

}