package com.qindashuai.toolkit.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    PARAM_ERROR(400, "参数错误"),
    TOO_MANY_REQUESTS(429, "请求过于频繁"),
    INTERNAL_ERROR(500, "系统内部错误"),
    BUSINESS_ERROR(600, "业务异常"),
    TOKEN_EXPIRED(700, "Token已过期"),
    TOKEN_INVALID(701, "Token无效"),
    IDEMPOTENT_ERROR(800, "重复请求"),
    LOCK_ACQUIRE_FAIL(900, "获取锁失败");

    private final int code;
    private final String message;

    public static ResultCode fromCode(int code) {
        for (ResultCode rc : ResultCode.values()) {
            if (rc.code == code) {
                return rc;
            }
        }
        return FAIL;
    }
}
