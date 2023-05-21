package io.quarkus.eureka;

import io.quarkus.eureka.config.EurekaBuildTimeConfiguration;

import java.util.function.BooleanSupplier;

class IsHealthEnabled implements BooleanSupplier {

    private final EurekaBuildTimeConfiguration eurekaBuildTimeConfiguration;

    IsHealthEnabled(EurekaBuildTimeConfiguration eurekaBuildTimeConfiguration) {
        this.eurekaBuildTimeConfiguration = eurekaBuildTimeConfiguration;
    }

    @Override
    public boolean getAsBoolean() {
        return eurekaBuildTimeConfiguration.heartBeat().enabled();
    }
}