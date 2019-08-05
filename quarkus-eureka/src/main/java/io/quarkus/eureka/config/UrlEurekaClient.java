package io.quarkus.eureka.config;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;


public class UrlEurekaClient {


    private Invocation.Builder invocationBuilder;

    private final Map<String, Object> instance;

    private String info;

    public UrlEurekaClient(final EurekaConfiguration eurekaConfiguration) {
        Client client = ClientBuilder.newClient();
        invocationBuilder = client.target(
                eurekaConfiguration.serviceUrl.get("default") + "/apps/" + eurekaConfiguration.name.toUpperCase()
        ).request(MediaType.APPLICATION_JSON);

        this.instance = EurekaRegisterConfig.from(eurekaConfiguration).getMap();
        buildInfo(eurekaConfiguration);
    }

    public Response register() {
        return invocationBuilder.post(Entity.entity(instance, MediaType.APPLICATION_JSON));
    }

    private void buildInfo(final EurekaConfiguration eurekaConfiguration) {
        this.info = "url: " + eurekaConfiguration.serviceUrl.get("default");
    }

    public String getInfo() {
        return info;
    }
}
