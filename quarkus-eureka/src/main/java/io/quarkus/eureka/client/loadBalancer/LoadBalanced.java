package io.quarkus.eureka.client.loadBalancer;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Qualifier
public @interface LoadBalanced {
    @Nonbinding LoadBalancerType type() default LoadBalancerType.RANDOM;
}
