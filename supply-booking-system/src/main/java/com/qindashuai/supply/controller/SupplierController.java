package com.qindashuai.supply.controller;

import com.qindashuai.supply.common.PageResult;
import com.qindashuai.supply.common.Result;
import com.qindashuai.supply.dto.SupplierDTO;
import com.qindashuai.supply.service.SupplierService;
import com.qindashuai.supply.vo.SupplierVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/supplier")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    public Result<Long> create(@Valid @RequestBody SupplierDTO dto) {
        return Result.success(supplierService.createSupplier(dto));
    }

    @PutMapping
    public Result<Void> update(@Valid @RequestBody SupplierDTO dto) {
        supplierService.updateSupplier(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<SupplierVO> getById(@PathVariable Long id) {
        return Result.success(supplierService.getSupplierById(id));
    }

    @GetMapping("/page")
    public Result<PageResult<SupplierVO>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String supplierName,
            @RequestParam(required = false) Integer status) {
        return Result.success(supplierService.pageList(pageNum, pageSize, supplierName, status));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        supplierService.updateStatus(id, status);
        return Result.success();
    }

    @GetMapping("/active")
    public Result<?> getAllActive() {
        return Result.success(supplierService.getAllActiveSuppliers());
    }
}
