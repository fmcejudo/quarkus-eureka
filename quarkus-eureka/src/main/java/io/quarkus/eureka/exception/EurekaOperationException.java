package io.quarkus.eureka.exception;

public class EurekaOperationException extends RuntimeException {

    public EurekaOperationException(final String message) {
        super(message);
    }
}
