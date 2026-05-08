package com.qinyoucheng.toolkit.ratelimit;

import com.qinyoucheng.toolkit.redis.RedisAutoConfiguration;
import com.qinyoucheng.toolkit.redis.RedisUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(RedisAutoConfiguration.class)
public class RateLimiterAutoConfiguration {

    @Bean
    @ConditionalOnBean(RedisUtil.class)
    @ConditionalOnMissingBean
    public RateLimiterAspect rateLimiterAspect(RedisUtil redisUtil) {
        return new RateLimiterAspect(redisUtil);
    }
}
