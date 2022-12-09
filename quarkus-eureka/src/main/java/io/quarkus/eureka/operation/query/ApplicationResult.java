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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.List;
import java.util.Optional;

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
        this.instanceResults = Optional.ofNullable(instanceResults).orElse(List.of());
    }

    public static ApplicationResult error() {
        return new ApplicationResult("", emptyList());
    }

    @Override
    public boolean success() {
        return instanceResults.stream().anyMatch(InstanceResult::success);
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
