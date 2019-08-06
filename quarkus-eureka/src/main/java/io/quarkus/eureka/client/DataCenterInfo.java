package io.quarkus.eureka.client;

import javax.json.bind.annotation.JsonbProperty;

public interface DataCenterInfo {

    enum Name {
        MyOwn
    }

    @JsonbProperty("@class")
    default String getClassName() {
        return "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo";
    }

    Name getName();
}
