package io.quarkus.eureka.operation.query;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.List;

import static java.util.Collections.emptyList;

@JsonRootName("application")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationResult extends QueryResponse<ApplicationResult> {

    private final String name;
    private final List<InstanceResult> instanceResults;

    @JsonCreator
    public ApplicationResult(@JsonProperty("name") final String name,
                             @JsonProperty("instance") final List<InstanceResult> instanceResults) {
        this.name = name;
        this.instanceResults = instanceResults;
    }

    public static ApplicationResult error() {
        return new ApplicationResult("", emptyList());
    }

    @Override
    boolean success() {
        return false;
    }

    @Override
    ApplicationResult entity() {
        return this;
    }

    public String getName() {
        return name;
    }

    public List<InstanceResult> getInstanceResults() {
        return instanceResults;
    }
}
