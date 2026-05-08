package com.qinyoucheng.rag.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatResponseDTO {

    private String conversationId;

    private String answer;

    private String intentType;

    private List<SourceReference> sources;

    private Integer responseTime;

    @Data
    public static class SourceReference {
        private Long chunkId;
        private String content;
        private Double score;
        private String documentTitle;
    }
}
