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

package io.quarkus.eureka.operation.heartbeat;

import io.quarkus.eureka.config.Location;
import io.quarkus.eureka.client.InstanceInfo;
import io.quarkus.eureka.operation.AbstractOperation;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.logging.Logger;

import static io.quarkus.eureka.client.Status.UP;
import static java.lang.String.format;
import static java.util.Collections.singletonMap;

public class HeartBeatOperation extends AbstractOperation {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public void heartbeat(final Location location, final InstanceInfo instanceInfo) {
        String appId = instanceInfo.getApp();
        logger.info(format("%s heartbeat to %s", appId, location));
        final String path = String.join("/", "apps", appId, instanceInfo.getInstanceId());
        Map<String, InstanceInfo> instance = singletonMap("instance", instanceInfo.withStatus(UP));
        Client client = ResteasyClientBuilder.newClient();

        try {
            this.restClientBuilder(client, location, path)
                    .put(Entity.entity(instance, MediaType.APPLICATION_JSON_TYPE))
                    .close();
        } catch (ProcessingException e) {
            logger.info(format("remote endpoint %s does not response", String.join("/", location.getUrl(), path)));
        } finally {
            client.close();
        }
    }
}
