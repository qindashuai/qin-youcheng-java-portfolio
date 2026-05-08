package com.qindashuai.supply.service;

import com.qindashuai.supply.common.PageResult;
import com.qindashuai.supply.entity.ColdChainVehicle;

public interface VehicleService {

    Long addVehicle(ColdChainVehicle vehicle);

    void updateVehicle(ColdChainVehicle vehicle);

    void deleteVehicle(Long id);

    ColdChainVehicle getVehicleById(Long id);

    PageResult<ColdChainVehicle> pageList(Integer pageNum, Integer pageSize, Long supplierId, Integer status);
}
