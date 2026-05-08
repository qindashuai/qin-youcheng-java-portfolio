package com.qindashuai.toolkit.lock;

import com.qindashuai.toolkit.redis.RedisAutoConfiguration;
import com.qindashuai.toolkit.redis.RedisUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(RedisAutoConfiguration.class)
public class LockAutoConfiguration {

    @Bean
    @ConditionalOnBean(RedisUtil.class)
    @ConditionalOnMissingBean
    public DistributedLockAspect distributedLockAspect(RedisUtil redisUtil) {
        return new DistributedLockAspect(redisUtil);
    }
}
