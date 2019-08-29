package io.quarkus.eureka.operation.query;

import java.util.function.Consumer;

abstract class QueryResponse<T> {

    abstract boolean success();

    abstract T entity();

    public T isRegistered(final Consumer<T> statusConsumer) {
        if (success()) {
            statusConsumer.accept(entity());
        }
        return entity();
    }

    public T isNotRegistered(final Consumer<T> statusConsumer) {
        if (!success()) {
            statusConsumer.accept(entity());
        }
        return entity();
    }

}
