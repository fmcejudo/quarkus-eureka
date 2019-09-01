package io.quarkus.eureka.operation.query;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.Collections;
import java.util.List;

@JsonRootName("applications")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationsResult extends QueryResponse<ApplicationsResult> {

    private final List<ApplicationResult> applications;

    @JsonCreator
    public ApplicationsResult(
            @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            @JsonProperty(value = "application") final List<ApplicationResult> applications) {

        this.applications = applications;
    }

    public static ApplicationsResult error() {
        return new ApplicationsResult(Collections.emptyList());
    }

    @Override
    boolean success() {
        return applications.stream().anyMatch(ApplicationResult::success);
    }

    @Override
    ApplicationsResult entity() {
        return this;
    }

    public List<ApplicationResult> getApplications() {
        return applications;
    }

}
