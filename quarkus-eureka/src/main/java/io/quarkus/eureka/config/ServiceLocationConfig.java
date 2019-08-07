package io.quarkus.eureka.config;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public class ServiceLocationConfig {

    private final Collection<String> locations;

    public ServiceLocationConfig(@NotNull final EurekaConfiguration eurekaConfiguration) {
        this.locations = ofNullable(eurekaConfiguration.serviceUrl).map(Map::values).orElse(emptyList());
    }

    public ServiceLocationConfig(Collection<String> locations) {
        this.locations = locations;
    }

    public Collection<String> getLocations() {
        return locations;
    }
}
