package com.qindashuai.supply.common;

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

    SUPPLIER_NOT_FOUND(1001, "供应商不存在"),
    SUPPLIER_CODE_EXISTS(1002, "供应商编码已存在"),
    SUPPLIER_DISABLED(1003, "供应商已禁用"),

    QUALIFICATION_NOT_FOUND(1101, "资质不存在"),
    QUALIFICATION_EXPIRED(1102, "资质已过期"),
    QUALIFICATION_EXPIRING(1103, "资质即将过期"),

    BOOKING_NOT_FOUND(1201, "预约订单不存在"),
    BOOKING_SLOT_FULL(1202, "预约时段已满"),
    BOOKING_CONFLICT(1203, "预约时间冲突"),
    BOOKING_CANNOT_CANCEL(1204, "当前状态不允许取消"),
    BOOKING_CANNOT_CONFIRM(1205, "当前状态不允许确认"),

    TIMESLOT_NOT_FOUND(1301, "时间段不存在"),

    PARK_ENTRY_NOT_FOUND(1401, "入园记录不存在"),

    VEHICLE_NOT_FOUND(1501, "车辆信息不存在"),
    VEHICLE_NO_EXISTS(1502, "车牌号已存在"),

    RECEIVING_NOT_FOUND(1601, "收台记录不存在"),

    RATE_LIMIT_EXCEEDED(1701, "请求过于频繁，请稍后再试"),

    SYSTEM_ERROR(9999, "系统异常");

    private final int code;
    private final String message;
}
