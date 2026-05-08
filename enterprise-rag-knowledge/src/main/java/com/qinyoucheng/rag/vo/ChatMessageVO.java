package com.qinyoucheng.rag.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageVO {

    private Long id;

    private String conversationId;

    private String role;

    private String content;

    private String intentType;

    private String sourceChunks;

    private Integer tokenUsage;

    private Integer responseTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
