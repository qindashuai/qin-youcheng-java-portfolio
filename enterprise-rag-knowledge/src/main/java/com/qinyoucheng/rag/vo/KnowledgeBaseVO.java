package com.qinyoucheng.rag.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KnowledgeBaseVO {

    private Long id;

    private String name;

    private String description;

    private String category;

    private Integer documentCount;

    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
