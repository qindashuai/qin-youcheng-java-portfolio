package com.qindashuai.rag.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class FeedbackDTO {

    @NotNull(message = "消息ID不能为空")
    private Long messageId;

    private String conversationId;

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低1星")
    @Max(value = 5, message = "评分最高5星")
    private Integer score;

    private String comment;

    private String userId = "anonymous";
}
