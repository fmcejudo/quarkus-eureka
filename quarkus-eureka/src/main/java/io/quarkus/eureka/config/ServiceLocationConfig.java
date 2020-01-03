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

package io.quarkus.eureka.config;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public class ServiceLocationConfig {

    private final Collection<Location> locations;

    public ServiceLocationConfig(@NotNull final EurekaConfiguration eurekaConfiguration) {
       this(ofNullable(eurekaConfiguration.serviceUrl).map(Map::values).orElse(emptyList()));
    }

    public ServiceLocationConfig(Collection<String> locationsAsString) {


        this.locations = locationsAsString.stream().map(s -> new Location(s)).collect(Collectors.toList());
    }

    public Collection<Location> getLocations() {
        return locations;
    }
}
