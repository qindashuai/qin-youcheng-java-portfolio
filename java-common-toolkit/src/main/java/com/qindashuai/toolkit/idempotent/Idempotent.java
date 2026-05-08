package com.qindashuai.toolkit.idempotent;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    String key() default "";

    long expireTime() default 5L;

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    String message() default "重复请求，请稍后重试";

    boolean delKey() default false;
}
