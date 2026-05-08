package com.qindashuai.supply.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qindashuai.supply.common.BusinessException;
import com.qindashuai.supply.common.PageResult;
import com.qindashuai.supply.common.ResultCode;
import com.qindashuai.supply.dto.SupplierDTO;
import com.qindashuai.supply.entity.Supplier;
import com.qindashuai.supply.entity.SupplierQualification;
import com.qindashuai.supply.mapper.SupplierMapper;
import com.qindashuai.supply.mapper.SupplierQualificationMapper;
import com.qindashuai.supply.service.SupplierService;
import com.qindashuai.supply.util.RedisUtil;
import com.qindashuai.supply.vo.SupplierVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierMapper supplierMapper;
    private final SupplierQualificationMapper qualificationMapper;
    private final RedisUtil redisUtil;

    private static final String SUPPLIER_CACHE_KEY = "supply:supplier:";
    private static final long CACHE_EXPIRE_HOURS = 24;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSupplier(SupplierDTO dto) {
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Supplier::getSupplierCode, dto.getSupplierCode());
        if (supplierMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.SUPPLIER_CODE_EXISTS);
        }

        Supplier supplier = new Supplier();
        BeanUtil.copyProperties(dto, supplier);
        supplier.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        supplierMapper.insert(supplier);

        cacheSupplier(supplier);
        return supplier.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSupplier(SupplierDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        Supplier existing = supplierMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.SUPPLIER_NOT_FOUND);
        }

        if (!existing.getSupplierCode().equals(dto.getSupplierCode())) {
            LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Supplier::getSupplierCode, dto.getSupplierCode());
            if (supplierMapper.selectCount(wrapper) > 0) {
                throw new BusinessException(ResultCode.SUPPLIER_CODE_EXISTS);
            }
        }

        Supplier supplier = new Supplier();
        BeanUtil.copyProperties(dto, supplier);
        supplierMapper.updateById(supplier);

        redisUtil.delete(SUPPLIER_CACHE_KEY + dto.getId());
        cacheSupplier(supplierMapper.selectById(dto.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierMapper.selectById(id);
        if (supplier == null) {
            throw new BusinessException(ResultCode.SUPPLIER_NOT_FOUND);
        }
        supplierMapper.deleteById(id);
        redisUtil.delete(SUPPLIER_CACHE_KEY + id);
    }

    @Override
    public SupplierVO getSupplierById(Long id) {
        Supplier supplier = getSupplierFromCache(id);
        if (supplier == null) {
            supplier = supplierMapper.selectById(id);
            if (supplier == null) {
                throw new BusinessException(ResultCode.SUPPLIER_NOT_FOUND);
            }
            cacheSupplier(supplier);
        }

        SupplierVO vo = new SupplierVO();
        BeanUtil.copyProperties(supplier, vo);

        LambdaQueryWrapper<SupplierQualification> qWrapper = new LambdaQueryWrapper<>();
        qWrapper.eq(SupplierQualification::getSupplierId, id);
        List<SupplierQualification> qualifications = qualificationMapper.selectList(qWrapper);
        vo.setQualifications(qualifications.stream().map(q -> {
            SupplierVO.QualificationVO qvo = new SupplierVO.QualificationVO();
            qvo.setId(q.getId());
            qvo.setQualificationType(q.getQualificationType());
            qvo.setQualificationName(q.getQualificationName());
            qvo.setCertificateNo(q.getCertificateNo());
            qvo.setExpireDate(q.getExpireDate().toString());
            qvo.setStatus(q.getStatus());
            return qvo;
        }).collect(Collectors.toList()));

        return vo;
    }

    @Override
    public PageResult<SupplierVO> pageList(Integer pageNum, Integer pageSize, String supplierName, Integer status) {
        Page<Supplier> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(supplierName), Supplier::getSupplierName, supplierName);
        wrapper.eq(status != null, Supplier::getStatus, status);
        wrapper.orderByDesc(Supplier::getCreateTime);

        Page<Supplier> result = supplierMapper.selectPage(page, wrapper);
        List<SupplierVO> voList = result.getRecords().stream().map(s -> {
            SupplierVO vo = new SupplierVO();
            BeanUtil.copyProperties(s, vo);
            return vo;
        }).collect(Collectors.toList());

        return PageResult.of(result.getTotal(), pageNum, pageSize, voList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        Supplier supplier = supplierMapper.selectById(id);
        if (supplier == null) {
            throw new BusinessException(ResultCode.SUPPLIER_NOT_FOUND);
        }
        supplier.setStatus(status);
        supplierMapper.updateById(supplier);
        redisUtil.delete(SUPPLIER_CACHE_KEY + id);
    }

    @Override
    public List<Supplier> getAllActiveSuppliers() {
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Supplier::getStatus, 1);
        return supplierMapper.selectList(wrapper);
    }

    private Supplier getSupplierFromCache(Long id) {
        return (Supplier) redisUtil.get(SUPPLIER_CACHE_KEY + id);
    }

    private void cacheSupplier(Supplier supplier) {
        if (supplier != null) {
            redisUtil.set(SUPPLIER_CACHE_KEY + supplier.getId(), supplier, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        }
    }
}
