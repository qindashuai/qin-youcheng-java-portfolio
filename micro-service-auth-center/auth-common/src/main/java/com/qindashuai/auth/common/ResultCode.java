package com.qindashuai.auth.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),

    UNAUTHORIZED(401, "未认证，请先登录"),
    FORBIDDEN(403, "没有相关权限"),
    TOKEN_EXPIRED(401, "Token已过期"),
    TOKEN_INVALID(401, "Token无效"),
    TOKEN_MISSING(401, "Token缺失"),

    PARAM_ERROR(400, "参数错误"),
    PARAM_VALID_ERROR(400, "参数校验失败"),

    USER_NOT_FOUND(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "密码错误"),
    USER_DISABLED(1003, "用户已被禁用"),
    USER_LOCKED(1004, "用户已被锁定"),
    USER_ALREADY_EXISTS(1005, "用户已存在"),

    ROLE_NOT_FOUND(1101, "角色不存在"),
    ROLE_ALREADY_EXISTS(1102, "角色已存在"),
    ROLE_HAS_USERS(1103, "角色下存在用户，无法删除"),

    MENU_NOT_FOUND(1201, "菜单不存在"),

    BLACKLIST_EXISTS(1301, "已在黑名单中"),

    SYSTEM_NOT_REGISTERED(1401, "系统未注册"),
    APP_SECRET_ERROR(1402, "app_secret错误"),

    RATE_LIMIT_EXCEEDED(429, "请求过于频繁，请稍后再试"),
    SERVICE_DEGRADE(503, "服务降级中，请稍后再试"),

    TOO_MANY_LOGIN_ATTEMPTS(1501, "登录尝试次数过多，请稍后再试"),
    SSO_TICKET_INVALID(1502, "SSO Ticket无效或已过期");

    private final int code;
    private final String message;
}
