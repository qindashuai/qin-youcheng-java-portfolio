package com.qinyoucheng.supply.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SupplierVO {

    private Long id;

    private String supplierCode;

    private String supplierName;

    private String contactPerson;

    private String contactPhone;

    private String businessScope;

    private String address;

    private Integer status;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private List<QualificationVO> qualifications;

    @Data
    public static class QualificationVO {
        private Long id;
        private String qualificationType;
        private String qualificationName;
        private String certificateNo;
        private String expireDate;
        private Integer status;
    }
}
