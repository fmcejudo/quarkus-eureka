package io.quarkus.eureka.client;

interface DataCenterInfo {

    enum Name {
        MyOwn
    }

    Name getName();
}
