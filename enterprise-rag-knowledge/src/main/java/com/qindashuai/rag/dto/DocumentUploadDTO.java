package com.qindashuai.rag.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DocumentUploadDTO {

    @NotNull(message = "知识库ID不能为空")
    private Long knowledgeBaseId;

    private String title;

    private String chunkStrategy = "FIXED_SIZE";

    private Integer chunkSize = 512;

    private Integer chunkOverlap = 64;
}
