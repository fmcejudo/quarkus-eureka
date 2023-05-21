package io.quarkus.eureka.config;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;


@ConfigMapping(prefix = "quarkus")
@ConfigRoot(phase = ConfigPhase.BUILD_TIME)
public interface EurekaBuildTimeConfiguration {

    /**
     * heartbeat
     */
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
        @WithDefault(value = "/info/health")
        public String healthPath();

        /**
         * path to hit for status check.
         */
        @WithDefault(value = "/info/status")
        public String statusPath();
    }

}
