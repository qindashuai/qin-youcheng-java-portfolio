package com.qinyoucheng.supply.controller;

import com.qinyoucheng.supply.common.PageResult;
import com.qinyoucheng.supply.common.Result;
import com.qinyoucheng.supply.entity.ColdChainVehicle;
import com.qinyoucheng.supply.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vehicle")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public Result<Long> add(@RequestBody ColdChainVehicle vehicle) {
        return Result.success(vehicleService.addVehicle(vehicle));
    }

    @PutMapping
    public Result<Void> update(@RequestBody ColdChainVehicle vehicle) {
        vehicleService.updateVehicle(vehicle);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<ColdChainVehicle> getById(@PathVariable Long id) {
        return Result.success(vehicleService.getVehicleById(id));
    }

    @GetMapping("/page")
    public Result<PageResult<ColdChainVehicle>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) Integer status) {
        return Result.success(vehicleService.pageList(pageNum, pageSize, supplierId, status));
    }
}
