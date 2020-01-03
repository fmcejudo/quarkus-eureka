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

package io.quarkus.eureka.operation.register;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.eureka.client.InstanceInfo;
import io.quarkus.eureka.config.Location;
import io.quarkus.eureka.operation.AbstractOperation;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

import static io.quarkus.eureka.client.Status.UP;
import static java.lang.String.format;
import static java.util.Collections.singletonMap;
import static javax.ws.rs.core.Response.Status.Family.CLIENT_ERROR;
import static javax.ws.rs.core.Response.Status.Family.SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.Family.SUCCESSFUL;

public class RegisterOperation extends AbstractOperation {

    private Logger logger = Logger.getLogger(this.getClass());

    public void register(final Location location, final InstanceInfo instanceInfo) {
        String path = String.join("/", "apps", instanceInfo.getApp());
        Map<String, InstanceInfo> instance = singletonMap("instance", instanceInfo.withStatus(UP));
        Client client = ResteasyClientBuilder.newClient();

        try {
            Response response = this.restClientBuilder(client, location, path)
                    .accept(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(objectToJson(instance), MediaType.APPLICATION_JSON_TYPE));
            if (response.getStatusInfo().getFamily().equals(SUCCESSFUL)) {
                logger.info(format("Service has been registered in %s", location));
            } else if (response.getStatusInfo().getFamily().equals(CLIENT_ERROR)) {
                logger.info(format("Service has problems to register in %s", location));
            } else if (response.getStatusInfo().getFamily().equals(SERVER_ERROR)) {
                logger.info(format("%s returns error message %s", location, response.readEntity(String.class)));
            }
            response.close();
        } catch (ProcessingException ex) {
            logger.info("eureka service is down and no status can be register");
        } finally {
            client.close();
        }
    }

    private String objectToJson(Map<String, InstanceInfo> instance) {
        try {
            return new ObjectMapper().writeValueAsString(instance);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}