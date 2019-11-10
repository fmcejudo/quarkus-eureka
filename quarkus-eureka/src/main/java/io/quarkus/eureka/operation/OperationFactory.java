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

package io.quarkus.eureka.operation;

import io.quarkus.eureka.exception.EurekaOperationException;

import java.util.Arrays;
import java.util.List;

public class OperationFactory {

    private final List<Operation> operationList;

    public OperationFactory(final List<Operation> operationList) {
        this.operationList = Arrays.asList(operationList.toArray(new Operation[0]));
    }

    public <T extends Operation> T get(final Class<T> instanceClass) {
        if (instanceClass.isInterface()) {
            throw new EurekaOperationException("Operation must be an implementation");
        }

        return operationList.stream()
                .filter(instanceClass::isInstance)
                .map(instanceClass::cast)
                .findFirst()
                .orElseThrow(() ->
                        new EurekaOperationException("Operation of instance " + instanceClass.getName() + " not found")
                );
    }


}
