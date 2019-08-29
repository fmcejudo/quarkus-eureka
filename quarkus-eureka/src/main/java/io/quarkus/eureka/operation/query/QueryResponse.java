package io.quarkus.eureka.operation.query;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import java.util.function.Consumer;

abstract class QueryResponse<T> {

    abstract boolean success();

    abstract T entity();

    public T onSuccess(final Consumer<T> statusConsumer) {
        if (success()) {
            statusConsumer.accept(entity());
        }
        return entity();
    }

    public T onError(final Consumer<T> statusConsumer) {
        if (!success()) {
            statusConsumer.accept(entity());
        }
        return entity();
    }

}
