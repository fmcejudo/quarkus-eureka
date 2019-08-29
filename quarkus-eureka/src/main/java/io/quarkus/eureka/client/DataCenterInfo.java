package io.quarkus.eureka.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface DataCenterInfo {

    enum Name {
        MyOwn
    }

    @JsonProperty("@class")
    default String getClassName() {
        return "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo";
    }

    Name getName();
}
