package com.account_processing.aspect.annotations;

import java.lang.annotation.*;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cached {
    String cache() default "";

    long ttlMs() default -1;

    boolean cacheNull() default false;
}
