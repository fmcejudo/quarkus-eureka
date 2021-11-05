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

package io.quarkus.eureka.client.loadBalancer;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.quarkus.eureka.util.ServiceDiscovery;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class RoundRobin implements LoadBalancer {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final AtomicInteger position = new AtomicInteger();
    private final LoadingCache<String, List<String>> cache;

    public RoundRobin(ServiceDiscovery serviceDiscovery) {
        CacheLoader<String, List<String>> loader = new CacheLoader<String, List<String>>() {
            @Override
            public List<String> load(String key) {
                return serviceDiscovery.findServiceLocations(key).collect(Collectors.toList());
            }
        };
        this.cache = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofSeconds(60))
                .build(loader);
    }

    @Override
    public Optional<String> getHomeUrl(String appId) {
        String target = null;
        try {
            List<String> urlList = cache.get(appId);
            if (!urlList.isEmpty()) {
                if (position.intValue() > urlList.size() - 1) {
                    position.set(0);
                }
                target = urlList.get(position.getAndIncrement());
                logger.info("Target from RoundRobin LB is: " + target);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(target);
    }
}
