package io.quarkus.eureka.exception;

import static java.lang.String.format;

public class EurekaServiceNotFoundException extends RuntimeException {
    public EurekaServiceNotFoundException(String appId) {
        super(format("service %s not found", appId));
    }
}
