package com.qinyoucheng.auth.system.aspect;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    String value() default "";

    String operationType() default "QUERY";
}
