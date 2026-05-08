package com.qinyoucheng.supply.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qinyoucheng.supply.common.BusinessException;
import com.qinyoucheng.supply.common.PageResult;
import com.qinyoucheng.supply.common.ResultCode;
import com.qinyoucheng.supply.entity.ColdChainVehicle;
import com.qinyoucheng.supply.mapper.ColdChainVehicleMapper;
import com.qinyoucheng.supply.service.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final ColdChainVehicleMapper vehicleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addVehicle(ColdChainVehicle vehicle) {
        LambdaQueryWrapper<ColdChainVehicle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ColdChainVehicle::getVehicleNo, vehicle.getVehicleNo())
                .eq(ColdChainVehicle::getSupplierId, vehicle.getSupplierId());
        if (vehicleMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.VEHICLE_NO_EXISTS);
        }

        if (vehicle.getStatus() == null) {
            vehicle.setStatus(1);
        }
        vehicleMapper.insert(vehicle);
        return vehicle.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateVehicle(ColdChainVehicle vehicle) {
        ColdChainVehicle existing = vehicleMapper.selectById(vehicle.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.VEHICLE_NOT_FOUND);
        }

        if (vehicle.getVehicleNo() != null && !vehicle.getVehicleNo().equals(existing.getVehicleNo())) {
            LambdaQueryWrapper<ColdChainVehicle> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ColdChainVehicle::getVehicleNo, vehicle.getVehicleNo())
                    .eq(ColdChainVehicle::getSupplierId, existing.getSupplierId());
            if (vehicleMapper.selectCount(wrapper) > 0) {
                throw new BusinessException(ResultCode.VEHICLE_NO_EXISTS);
            }
        }

        vehicleMapper.updateById(vehicle);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteVehicle(Long id) {
        ColdChainVehicle existing = vehicleMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.VEHICLE_NOT_FOUND);
        }
        vehicleMapper.deleteById(id);
    }

    @Override
    public ColdChainVehicle getVehicleById(Long id) {
        ColdChainVehicle vehicle = vehicleMapper.selectById(id);
        if (vehicle == null) {
            throw new BusinessException(ResultCode.VEHICLE_NOT_FOUND);
        }
        return vehicle;
    }

    @Override
    public PageResult<ColdChainVehicle> pageList(Integer pageNum, Integer pageSize, Long supplierId, Integer status) {
        Page<ColdChainVehicle> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ColdChainVehicle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(supplierId != null, ColdChainVehicle::getSupplierId, supplierId);
        wrapper.eq(status != null, ColdChainVehicle::getStatus, status);
        wrapper.orderByDesc(ColdChainVehicle::getCreateTime);

        Page<ColdChainVehicle> result = vehicleMapper.selectPage(page, wrapper);
        return PageResult.of(result.getTotal(), pageNum, pageSize, result.getRecords());
    }
}
