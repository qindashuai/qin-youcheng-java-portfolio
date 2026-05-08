package com.qindashuai.supply.controller;

import com.qindashuai.supply.common.PageResult;
import com.qindashuai.supply.common.Result;
import com.qindashuai.supply.entity.SupplierQualification;
import com.qindashuai.supply.service.QualificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/qualification")
@RequiredArgsConstructor
public class QualificationController {

    private final QualificationService qualificationService;

    @PostMapping
    public Result<Long> add(@RequestBody SupplierQualification qualification) {
        return Result.success(qualificationService.addQualification(qualification));
    }

    @PutMapping
    public Result<Void> update(@RequestBody SupplierQualification qualification) {
        qualificationService.updateQualification(qualification);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        qualificationService.deleteQualification(id);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult<SupplierQualification>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) Integer status) {
        return Result.success(qualificationService.pageList(pageNum, pageSize, supplierId, status));
    }

    @GetMapping("/expiring")
    public Result<?> getExpiring(@RequestParam(defaultValue = "3") int days) {
        return Result.success(qualificationService.getExpiringQualifications(days));
    }
}
