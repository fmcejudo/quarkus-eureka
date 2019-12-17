/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.quarkus.eureka.operation.query;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.quarkus.eureka.operation.AbstractOperation;
import io.quarkus.eureka.operation.Operation;
import org.apache.http.HttpStatus;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

abstract class QueryOperation extends AbstractOperation {

    <T> T query(final String location, final String path, Class<T> clazz) {
        Client client = ResteasyClientBuilder.newClient();
        Response response = this.restClientBuilder(client, location, path).get();

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
