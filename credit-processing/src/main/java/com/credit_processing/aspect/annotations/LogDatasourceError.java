package com.credit_processing.aspect.annotations;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface LogDatasourceError {
    String value() default "ERROR";
}