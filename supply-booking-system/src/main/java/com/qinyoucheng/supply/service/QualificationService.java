package com.qinyoucheng.supply.service;

import com.qinyoucheng.supply.common.PageResult;
import com.qinyoucheng.supply.entity.SupplierQualification;

import java.util.List;

public interface QualificationService {

    Long addQualification(SupplierQualification qualification);

    void updateQualification(SupplierQualification qualification);

    void deleteQualification(Long id);

    PageResult<SupplierQualification> pageList(Integer pageNum, Integer pageSize, Long supplierId, Integer status);

    List<SupplierQualification> getExpiringQualifications(int days);

    void checkAndWarnExpiringQualifications();
}
