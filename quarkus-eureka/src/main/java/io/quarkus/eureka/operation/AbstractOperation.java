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

package io.quarkus.eureka.operation;

import io.quarkus.eureka.config.Location;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.MediaType;


public abstract class AbstractOperation implements Operation {

    protected Builder restClientBuilder(final Client client, final Location location, final String path) {

        Builder builder = client.target(String.join("/", location.getUrl(), path))
                .request(MediaType.APPLICATION_JSON_TYPE);

        setAuthHeaderIfNeeded(builder, location);

        return builder;
    }

    private void setAuthHeaderIfNeeded(Builder builder, Location location) {
    
        if (location.hasBasicAuthToken()) {
            builder.header("Authorization", "Basic " + location.getBasicAuthToken());
        }
    }    
}
