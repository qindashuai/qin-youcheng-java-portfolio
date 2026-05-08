package com.qindashuai.rag.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ChatRequestDTO {

    @NotBlank(message = "问题内容不能为空")
    private String question;

    private String conversationId;

    private Long knowledgeBaseId;

    private String userId = "anonymous";
}
