package com.qindashuai.rag.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("knowledge_document")
public class KnowledgeDocument {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long knowledgeBaseId;

    private String title;

    private String fileName;

    private String filePath;

    private String fileType;

    private Long fileSize;

    private Integer chunkCount;

    private Integer vectorStatus;

    private Integer parseStatus;

    private String chunkStrategy;

    private Integer chunkSize;

    private Integer chunkOverlap;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
