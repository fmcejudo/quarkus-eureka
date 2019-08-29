package io.quarkus.eureka.operation.query;

public class MultipleInstanceQueryOperation extends QueryOperation {

    public ApplicationsResult findAllInstances(final String location) {
        final String path = "apps";
        return query(location, path, ApplicationsResult.class);
    }

    public ApplicationResult findInstance(final String location, final String appId) {
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
