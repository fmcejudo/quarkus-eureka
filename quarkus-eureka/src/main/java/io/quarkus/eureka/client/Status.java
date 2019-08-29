package io.quarkus.eureka.client;

public enum Status {

    STARTING, UP, DOWN, UNKNOWN;

    /*private static Map<String, Status> statusMap;
    static {
        statusMap = Stream.of(Status.values()).collect(Collectors.toMap(
                Status::name,
                s -> s
        ));
    }

    public static Status forValue(String value) {
        return Optional.ofNullable(statusMap.get(value.toUpperCase())).orElse(UNKNOWN);
    }*/

}
