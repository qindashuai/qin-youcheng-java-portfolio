package com.qinyoucheng.toolkit.lock;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {

    String key() default "";

    long leaseTime() default 30000L;

    long waitTime() default 3000L;

    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    String errorMessage() default "获取分布式锁失败，请稍后重试";
}
