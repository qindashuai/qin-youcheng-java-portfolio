package com.qindashuai.toolkit.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "toolkit.redis")
public class RedisProperties {

    private String keyPrefix = "toolkit:";
    private long defaultExpiration = 3600L;
    private long lockWaitTimeout = 3000L;
    private long lockLeaseTime = 30000L;
}
