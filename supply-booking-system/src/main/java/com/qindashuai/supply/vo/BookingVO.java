package com.qindashuai.supply.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookingVO {

    private Long id;

    private String bookingNo;

    private Long supplierId;

    private String supplierName;

    private Long timeSlotId;

    private LocalDate bookingDate;

    private String bookingTime;

    private String vehicleNo;

    private String driverName;

    private String driverPhone;

    private String goodsType;

    private BigDecimal goodsQuantity;

    private Integer status;

    private String statusDesc;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime confirmTime;

    private String cancelReason;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    public static String getStatusDesc(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "待确认";
            case 1: return "已确认";
            case 2: return "已入园";
            case 3: return "已完成";
            case 4: return "已取消";
            default: return "未知";
        }
    }
}
