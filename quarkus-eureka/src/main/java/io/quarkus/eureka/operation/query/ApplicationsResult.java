/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
