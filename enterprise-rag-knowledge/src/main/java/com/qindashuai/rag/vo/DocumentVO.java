package com.qindashuai.rag.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DocumentVO {

    private Long id;

    private Long knowledgeBaseId;

    private String title;

    private String fileName;

    private String fileType;

    private Long fileSize;

    private Integer chunkCount;

    private Integer vectorStatus;

    private Integer parseStatus;

    private String chunkStrategy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
