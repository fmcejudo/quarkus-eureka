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
                //.refreshAfterWrite(Duration.ofSeconds(30))
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
