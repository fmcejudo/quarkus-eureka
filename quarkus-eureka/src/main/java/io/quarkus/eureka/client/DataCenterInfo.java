package io.quarkus.eureka.client;

public interface DataCenterInfo {

    enum Name {
        MyOwn
    }

    Name getName();
}
