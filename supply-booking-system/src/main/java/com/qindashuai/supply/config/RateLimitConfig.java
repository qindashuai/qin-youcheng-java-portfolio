package com.qindashuai.supply.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "supply.rate-limit")
public class RateLimitConfig {

    private boolean enabled = true;
    private int defaultCapacity = 100;
    private int defaultTokens = 100;
    private int defaultRate = 10;
}
