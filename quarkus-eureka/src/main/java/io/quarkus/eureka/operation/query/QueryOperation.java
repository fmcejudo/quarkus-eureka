package io.quarkus.eureka.operation.query;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.quarkus.eureka.operation.Operation;
import org.apache.http.HttpStatus;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

abstract class QueryOperation implements Operation {

    <T> T query(final String location, final String path, Class<T> clazz) {
        Client client = ResteasyClientBuilder.newClient();
        Response response = client.target(String.join("/", location, path))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        if (response.getStatus() == HttpStatus.SC_NOT_FOUND) {
            return this.onNotFound(clazz);
        }

        String json = response.readEntity(String.class);
        response.close();
        client.close();
        return jsonToObject(clazz, json);
    }

    private <T> T jsonToObject(Class<T> clazz, String json) {
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

    abstract <T> T onNotFound(Class<T> clazz);

    abstract <T> void onError(Class<T> clazz);
}
