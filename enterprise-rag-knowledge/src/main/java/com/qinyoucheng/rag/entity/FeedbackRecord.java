package com.qinyoucheng.rag.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("feedback_record")
public class FeedbackRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long messageId;

    private String conversationId;

    private String userId;

    private Integer score;

    private String comment;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
