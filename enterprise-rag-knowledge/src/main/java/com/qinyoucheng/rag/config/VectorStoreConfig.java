package com.qinyoucheng.rag.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "rag.vector")
public class VectorStoreConfig {

    private int topK = 5;
    private double similarityThreshold = 0.6;
}
