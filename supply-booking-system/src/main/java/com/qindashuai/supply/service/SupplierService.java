package com.qindashuai.supply.service;

import com.qindashuai.supply.common.PageResult;
import com.qindashuai.supply.dto.SupplierDTO;
import com.qindashuai.supply.entity.Supplier;
import com.qindashuai.supply.vo.SupplierVO;

import java.util.List;

public interface SupplierService {

    Long createSupplier(SupplierDTO dto);

    void updateSupplier(SupplierDTO dto);

    void deleteSupplier(Long id);

    SupplierVO getSupplierById(Long id);

    PageResult<SupplierVO> pageList(Integer pageNum, Integer pageSize, String supplierName, Integer status);

    void updateStatus(Long id, Integer status);

    List<Supplier> getAllActiveSuppliers();
}
