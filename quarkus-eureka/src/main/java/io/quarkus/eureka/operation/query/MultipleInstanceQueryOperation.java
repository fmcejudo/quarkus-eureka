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

import io.quarkus.eureka.config.Location;

public class MultipleInstanceQueryOperation extends QueryOperation {

    public ApplicationsResult findAllInstances(final Location location) {
        final String path = "apps";
        return query(location, path, ApplicationsResult.class);
    }

    public ApplicationResult findInstance(final Location location, final String appId) {
        final String path = String.join("/", "apps", appId);
        return query(location, path, ApplicationResult.class);
    }

    @Override
    <T> T onNotFound(Class<T> clazz) {
        if (clazz.equals(ApplicationResult.class)) {
            return (T)ApplicationResult.error();
        } else if( clazz.equals(ApplicationsResult.class) ){
            return (T) ApplicationsResult.error();
        }
        throw new RuntimeException("Class not managed by this operation");
    }

    @Override
    <T> void onError(Class<T> clazz) {
        throw new RuntimeException("there is a client or server error");
    }

}
