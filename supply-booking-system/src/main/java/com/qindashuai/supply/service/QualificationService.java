package com.qindashuai.supply.service;

import com.qindashuai.supply.common.PageResult;
import com.qindashuai.supply.entity.SupplierQualification;

import java.util.List;

public interface QualificationService {

    Long addQualification(SupplierQualification qualification);

    void updateQualification(SupplierQualification qualification);

    void deleteQualification(Long id);

    PageResult<SupplierQualification> pageList(Integer pageNum, Integer pageSize, Long supplierId, Integer status);

    List<SupplierQualification> getExpiringQualifications(int days);

    void checkAndWarnExpiringQualifications();
}
