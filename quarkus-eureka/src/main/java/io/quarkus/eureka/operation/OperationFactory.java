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
