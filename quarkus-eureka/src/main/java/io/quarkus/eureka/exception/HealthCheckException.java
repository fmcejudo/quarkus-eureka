package io.quarkus.eureka.exception;

public class HealthCheckException extends RuntimeException {

    public HealthCheckException(final String message) {
        super(message);
    }
}
