package io.quarkus.eureka.operation.query;

import io.quarkus.eureka.operation.Operation;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

interface QueryOperation extends Operation {

    default <T> T query(final String location, final String path, Class<T> clazz) {
        Client client = ResteasyClientBuilder.newClient();
        Response response = client.target(String.join("/", location, path))
                .register(ResteasyJackson2Provider.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();
        T t = response.readEntity(clazz);
        client.close(); //client closes but it is not Closeable. I can't use try-with-resources
        return t;
    }
}
