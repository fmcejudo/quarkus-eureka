package io.quarkus.eureka.config;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;


@ConfigMapping(prefix = "quarkus.eureka")
@ConfigRoot(phase = ConfigPhase.BUILD_TIME)
public interface EurekaBuildTimeConfiguration {

    /**
     * heartbeat
     */
    @WithName("heartbeat")
    public HeartBeat heartBeat();


    interface HeartBeat {
        /**
         * enable the health heartbeat
         */
        @WithDefault(value = "true")
        public boolean enabled();

        /**
         * path to hit for health.
         */
        @WithName("health-path")
        @WithDefault(value = "/info/health")
        public String healthPath();

        /**
         * path to hit for status check.
         */
        @WithDefault(value = "/info/status")
        public String statusPath();
    }

}
