package com.qindashuai.toolkit.ratelimit;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {

    int permits() default 10;

    long period() default 1;

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    LimitType limitType() default LimitType.IP;

    String key() default "";

    String message() default "请求过于频繁，请稍后重试";

    enum TimeUnit {
        SECONDS,
        MINUTES,
        HOURS
    }

    enum LimitType {
        IP,
        USER,
        INTERFACE,
        CUSTOM
    }
}
