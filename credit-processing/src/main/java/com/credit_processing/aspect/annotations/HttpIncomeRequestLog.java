package com.credit_processing.aspect.annotations;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface HttpIncomeRequestLog {
    String value() default "INFO";
}
