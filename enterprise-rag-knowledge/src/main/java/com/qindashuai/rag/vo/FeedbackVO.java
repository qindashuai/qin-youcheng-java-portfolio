package com.qindashuai.rag.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FeedbackVO {

    private Long id;

    private Long messageId;

    private String conversationId;

    private String userId;

    private Integer score;

    private String comment;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
