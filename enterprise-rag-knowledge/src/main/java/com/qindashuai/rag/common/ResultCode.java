package com.qindashuai.rag.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "系统内部错误"),

    DOCUMENT_NOT_FOUND(1001, "文档不存在"),
    DOCUMENT_PARSE_ERROR(1002, "文档解析失败"),
    DOCUMENT_UPLOAD_ERROR(1003, "文档上传失败"),
    DOCUMENT_TYPE_NOT_SUPPORT(1004, "不支持的文档类型"),

    CHUNK_SPLIT_ERROR(1101, "文档分块失败"),

    VECTOR_EMBEDDING_ERROR(1201, "向量化失败"),
    VECTOR_SEARCH_ERROR(1202, "向量检索失败"),

    CHAT_ERROR(1301, "问答服务异常"),
    INTENT_RECOGNIZE_ERROR(1302, "意图识别失败"),
    LLM_CALL_ERROR(1303, "LLM调用失败"),

    KNOWLEDGE_BASE_NOT_FOUND(1401, "知识库不存在"),
    KNOWLEDGE_BASE_EXISTS(1402, "知识库已存在"),

    FEEDBACK_ERROR(1501, "反馈提交失败");

    private final int code;
    private final String message;
}
