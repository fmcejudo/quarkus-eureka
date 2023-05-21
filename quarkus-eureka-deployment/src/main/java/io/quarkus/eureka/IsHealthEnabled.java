package io.quarkus.eureka;

import org.eclipse.microprofile.config.ConfigProvider;

import java.util.function.BooleanSupplier;

class IsHealthEnabled implements BooleanSupplier {

    private static final String HEART_BEAT_KEY = "quarkus.eureka.heartbeat.enabled";

    @Override
    public boolean getAsBoolean() {

        return ConfigProvider.getConfig().getOptionalValue(HEART_BEAT_KEY, Boolean.class).orElse(false);

    }
}