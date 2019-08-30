package io.quarkus.eureka.operation.query;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.eureka.client.Status;

import static io.quarkus.eureka.client.Status.UNKNOWN;
import static io.quarkus.eureka.client.Status.UP;



@JsonIgnoreProperties(ignoreUnknown = true)
public class InstanceResult extends QueryResponse<InstanceResult> {

    private final String hostName;
    private final String app;
    private final String homePageUrl;
    private final String ipAddr;
    private final Status status;

    @JsonCreator
    public InstanceResult(@JsonProperty("hostName") final String hostName,
                          @JsonProperty("app") String app,
                          @JsonProperty("homePageUrl") String homePageUrl,
                          @JsonProperty("ipAddr") String ipAddr,
                          @JsonProperty("status") Status status) {
        this.hostName = hostName;
        this.app = app;
        this.ipAddr = ipAddr;
        this.status = status;
        this.homePageUrl = homePageUrl;
    }

    private InstanceResult(final Status status) {
        this(null, null, null, null, status);
    }

    public static InstanceResult error() {
        return new InstanceResult(UNKNOWN);
    }

    public Status getStatus() {
        return this.status;
    }

    @Override
    boolean success() {
        return UP.equals(this.getStatus());
    }

    @Override
    InstanceResult entity() {
        return this;
    }

    public String getHostName() {
        return hostName;
    }

    public String getHomePageUrl() {
        return homePageUrl;
    }

    public String getApp() {
        return app;
    }

    public String getIpAddr() {
        return ipAddr;
    }
}
