package com.qindashuai.toolkit.idempotent;

import com.qindashuai.toolkit.redis.RedisAutoConfiguration;
import com.qindashuai.toolkit.redis.RedisUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(RedisAutoConfiguration.class)
public class IdempotentAutoConfiguration {

    @Bean
    @ConditionalOnBean(RedisUtil.class)
    @ConditionalOnMissingBean
    public IdempotentAspect idempotentAspect(RedisUtil redisUtil) {
        return new IdempotentAspect(redisUtil);
    }
}
