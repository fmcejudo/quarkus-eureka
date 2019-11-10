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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OperationFactoryTest {

    private OperationFactory operationFactory;

    @BeforeEach
    public void setUp() {
        operationFactory = new OperationFactory(asList(OneOperation.instance(), TwoOperations.instance()));
    }

    @Test
    @DisplayName("it should retrieve an instance of class in the argument")
    public void shouldIdentifyEachOperation() {
        assertThat(operationFactory.get(OneOperation.class)).isInstanceOf(OneOperation.class);
        assertThat(operationFactory.get(TwoOperations.class)).isInstanceOf(TwoOperations.class);
    }

    @Test
    @DisplayName("it should throw a Eureka Operation Exception when no operation matches")
    public void shouldFailIfOperationIsNotDefined() {
        assertThatThrownBy(() -> operationFactory.get(EmptyOperation.class))
                .isInstanceOf(EurekaOperationException.class)
                .hasMessage("Operation of instance " + EmptyOperation.class.getName() + " not found");
    }

    @Test
    @DisplayName("it should retrieve implemented classes, but no interfaces")
    public void shouldFailIfOperationIsAnInterface() {
        assertThatThrownBy(() -> operationFactory.get(Operation.class))
                .isInstanceOf(EurekaOperationException.class)
                .hasMessage("Operation must be an implementation");
    }

    static class EmptyOperation implements Operation {
    }

    static class OneOperation implements Operation {
        static OneOperation instance() {
            return new OneOperation();
        }

    }

    static class TwoOperations implements Operation {
        static TwoOperations instance() {
            return new TwoOperations();
        }

    }

}