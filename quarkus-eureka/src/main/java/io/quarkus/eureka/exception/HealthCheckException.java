package io.quarkus.eureka.exception;

public class HealthCheckException extends RuntimeException {

    public HealthCheckException(final String message) {
        super(message);
    }

    public HealthCheckException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
