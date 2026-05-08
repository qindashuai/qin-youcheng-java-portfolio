package com.qinyoucheng.rag.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Data
@Configuration
@ConfigurationProperties(prefix = "rag.ollama")
public class OllamaConfig {

    private String baseUrl = "http://localhost:11434";
    private String chatModel = "deepseek-r1:7b";
    private String embeddingModel = "nomic-embed-text";

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return OllamaChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(chatModel)
                .timeout(Duration.ofMinutes(5))
                .temperature(0.7)
                .build();
    }

    @Bean
    public EmbeddingModel embeddingModel() {
        return OllamaEmbeddingModel.builder()
                .baseUrl(baseUrl)
                .modelName(embeddingModel)
                .timeout(Duration.ofMinutes(5))
                .build();
    }
}
