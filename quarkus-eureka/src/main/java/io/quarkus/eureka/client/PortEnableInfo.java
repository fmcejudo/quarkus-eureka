/*
 * Copyright 2019 the original author or authors.
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

package io.quarkus.eureka.client;

import com.fasterxml.jackson.annotation.JsonProperty;


public class PortEnableInfo {

    @JsonProperty("$")
    private final String port;
    @JsonProperty("@enabled")
    private final boolean enabled;

    private PortEnableInfo(String port, boolean enabled) {
        this.port = port;
        this.enabled = enabled;
    }

    static PortEnableInfo of(final int port, final boolean enabled) {
        return new PortEnableInfo(String.valueOf(port), enabled);
    }

    public String getPort() {
        return port;
    }

    public String getEnabled() {
        return String.valueOf(this.enabled);
    }
}
