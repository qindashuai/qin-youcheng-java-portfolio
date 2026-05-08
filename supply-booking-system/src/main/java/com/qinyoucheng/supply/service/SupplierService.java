package com.qinyoucheng.supply.service;

import com.qinyoucheng.supply.common.PageResult;
import com.qinyoucheng.supply.dto.SupplierDTO;
import com.qinyoucheng.supply.entity.Supplier;
import com.qinyoucheng.supply.vo.SupplierVO;

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
