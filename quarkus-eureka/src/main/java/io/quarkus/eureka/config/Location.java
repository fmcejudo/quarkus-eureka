/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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