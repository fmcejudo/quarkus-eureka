package io.quarkus.eureka.operation.query;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.quarkus.eureka.operation.Operation;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

interface QueryOperation extends Operation {

    default <T> T query(final String location, final String path, Class<T> clazz) {
        Client client = ResteasyClientBuilder.newClient();
        Response response = client.target(String.join("/", location, path))
                .register(ResteasyJackson2Provider.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();
        String json = response.readEntity(String.class);
        client.close(); //client closes but it is not Closeable. I can't use try-with-resources

        try {
            ObjectMapper objectMapper = new ObjectMapper()
                    .enable(DeserializationFeature.UNWRAP_ROOT_VALUE)
                    .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
                    .enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);

            return objectMapper
                    .readerFor(clazz)
                    .readValue(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
