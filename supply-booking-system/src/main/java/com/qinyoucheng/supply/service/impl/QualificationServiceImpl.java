package com.qinyoucheng.supply.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qinyoucheng.supply.common.BusinessException;
import com.qinyoucheng.supply.common.PageResult;
import com.qinyoucheng.supply.common.ResultCode;
import com.qinyoucheng.supply.config.RabbitMQConfig;
import com.qinyoucheng.supply.entity.Supplier;
import com.qinyoucheng.supply.entity.SupplierQualification;
import com.qinyoucheng.supply.mapper.SupplierMapper;
import com.qinyoucheng.supply.mapper.SupplierQualificationMapper;
import com.qinyoucheng.supply.service.QualificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class QualificationServiceImpl implements QualificationService {

    private final SupplierQualificationMapper qualificationMapper;
    private final SupplierMapper supplierMapper;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addQualification(SupplierQualification qualification) {
        Supplier supplier = supplierMapper.selectById(qualification.getSupplierId());
        if (supplier == null) {
            throw new BusinessException(ResultCode.SUPPLIER_NOT_FOUND);
        }

        LocalDate today = LocalDate.now();
        if (qualification.getExpireDate().isBefore(today)) {
            qualification.setStatus(0);
        } else if (qualification.getExpireDate().isBefore(today.plusDays(3))) {
            qualification.setStatus(2);
        } else {
            qualification.setStatus(1);
        }

        qualificationMapper.insert(qualification);
        return qualification.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateQualification(SupplierQualification qualification) {
        SupplierQualification existing = qualificationMapper.selectById(qualification.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.QUALIFICATION_NOT_FOUND);
        }

        LocalDate today = LocalDate.now();
        if (qualification.getExpireDate() != null) {
            if (qualification.getExpireDate().isBefore(today)) {
                qualification.setStatus(0);
            } else if (qualification.getExpireDate().isBefore(today.plusDays(3))) {
                qualification.setStatus(2);
            } else {
                qualification.setStatus(1);
            }
        }

        qualificationMapper.updateById(qualification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteQualification(Long id) {
        SupplierQualification existing = qualificationMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.QUALIFICATION_NOT_FOUND);
        }
        qualificationMapper.deleteById(id);
    }

    @Override
    public PageResult<SupplierQualification> pageList(Integer pageNum, Integer pageSize, Long supplierId, Integer status) {
        Page<SupplierQualification> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SupplierQualification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(supplierId != null, SupplierQualification::getSupplierId, supplierId);
        wrapper.eq(status != null, SupplierQualification::getStatus, status);
        wrapper.orderByAsc(SupplierQualification::getExpireDate);

        Page<SupplierQualification> result = qualificationMapper.selectPage(page, wrapper);
        return PageResult.of(result.getTotal(), pageNum, pageSize, result.getRecords());
    }

    @Override
    public List<SupplierQualification> getExpiringQualifications(int days) {
        LocalDate today = LocalDate.now();
        LocalDate warningDate = today.plusDays(days);

        LambdaQueryWrapper<SupplierQualification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SupplierQualification::getStatus, 1);
        wrapper.between(SupplierQualification::getExpireDate, today, warningDate);
        return qualificationMapper.selectList(wrapper);
    }

    @Override
    @Scheduled(cron = "0 0 8 * * ?")
    public void checkAndWarnExpiringQualifications() {
        log.info("开始检查资质过期预警...");
        List<SupplierQualification> expiringList = getExpiringQualifications(3);

        for (SupplierQualification qualification : expiringList) {
            qualification.setStatus(2);
            qualificationMapper.updateById(qualification);

            Supplier supplier = supplierMapper.selectById(qualification.getSupplierId());

            Map<String, Object> warningMsg = new HashMap<>();
            warningMsg.put("qualificationId", qualification.getId());
            warningMsg.put("supplierId", qualification.getSupplierId());
            warningMsg.put("supplierName", supplier != null ? supplier.getSupplierName() : "未知");
            warningMsg.put("qualificationName", qualification.getQualificationName());
            warningMsg.put("expireDate", qualification.getExpireDate().toString());
            warningMsg.put("daysRemaining", java.time.temporal.ChronoUnit.DAYS.between(
                    LocalDate.now(), qualification.getExpireDate()));

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.QUALIFICATION_WARNING_EXCHANGE,
                    RabbitMQConfig.QUALIFICATION_WARNING_ROUTING_KEY,
                    warningMsg
            );

            log.warn("资质预警: 供应商[{}], 资质[{}], 到期日期[{}]",
                    supplier != null ? supplier.getSupplierName() : "未知",
                    qualification.getQualificationName(),
                    qualification.getExpireDate());
        }

        List<SupplierQualification> expiredList = new LambdaQueryWrapper<SupplierQualification>()
                .lt(SupplierQualification::getExpireDate, LocalDate.now())
                .ne(SupplierQualification::getStatus, 0)
                .and(w -> w.eq(SupplierQualification::getStatus, 1).or().eq(SupplierQualification::getStatus, 2))
                .isNotNull(SupplierQualification::getExpireDate)
                .and(w -> w);
        List<SupplierQualification> expired = qualificationMapper.selectList(
                new LambdaQueryWrapper<SupplierQualification>()
                        .lt(SupplierQualification::getExpireDate, LocalDate.now())
                        .ne(SupplierQualification::getStatus, 0)
        );
        for (SupplierQualification qualification : expired) {
            qualification.setStatus(0);
            qualificationMapper.updateById(qualification);
            log.warn("资质已过期: 资质ID[{}], 资质名称[{}]", qualification.getId(), qualification.getQualificationName());
        }

        log.info("资质过期预警检查完成，即将过期: {}条，已过期: {}条", expiringList.size(), expired.size());
    }
}
