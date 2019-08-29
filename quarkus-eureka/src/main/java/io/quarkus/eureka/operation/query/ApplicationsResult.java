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

    private final String hashCode;

    @JsonCreator
    public ApplicationsResult(
            @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            @JsonProperty(value = "application") final List<ApplicationResult> applications,
            @JsonProperty(value = "apps__hashcode") final String hashCode) {

        this.applications = applications;
        this.hashCode = hashCode;
    }

    public static ApplicationsResult error() {
        return new ApplicationsResult(Collections.emptyList(), "");
    }

    @Override
    boolean success() {
        return false;
    }

    @Override
    ApplicationsResult entity() {
        return this;
    }

    public List<ApplicationResult> getApplications() {
        return applications;
    }

    public String getHashCode() {
        return hashCode;
    }
}
