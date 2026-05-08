package com.qindashuai.rag;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.qindashuai.rag.mapper")
@EnableAsync
public class RagKnowledgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(RagKnowledgeApplication.class, args);
    }
}
