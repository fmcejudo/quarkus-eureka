package io.quarkus.eureka.operation.query;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationResult extends QueryResponse<ApplicationResult> {

    private final String name;

    public ApplicationResult(@JsonProperty("name") final String name) {
        this.name = name;
    }

    public static ApplicationResult error() {
        return new ApplicationResult("");
    }

    @Override
    boolean success() {
        return false;
    }

    @Override
    ApplicationResult entity() {
        return this;
    }
}
