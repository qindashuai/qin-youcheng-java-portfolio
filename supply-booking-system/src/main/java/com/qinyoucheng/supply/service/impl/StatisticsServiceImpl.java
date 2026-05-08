package com.qinyoucheng.supply.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qinyoucheng.supply.dto.StatisticsDTO;
import com.qinyoucheng.supply.entity.BookingOrder;
import com.qinyoucheng.supply.entity.Supplier;
import com.qinyoucheng.supply.entity.SupplierQualification;
import com.qinyoucheng.supply.mapper.BookingOrderMapper;
import com.qinyoucheng.supply.mapper.SupplierMapper;
import com.qinyoucheng.supply.mapper.SupplierQualificationMapper;
import com.qinyoucheng.supply.service.StatisticsService;
import com.qinyoucheng.supply.vo.StatisticsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final BookingOrderMapper bookingOrderMapper;
    private final SupplierMapper supplierMapper;
    private final SupplierQualificationMapper qualificationMapper;

    @Override
    public StatisticsVO getOverviewStatistics(StatisticsDTO dto) {
        StatisticsVO vo = new StatisticsVO();

        LambdaQueryWrapper<BookingOrder> bookingWrapper = buildBookingWrapper(dto);
        List<BookingOrder> bookings = bookingOrderMapper.selectList(bookingWrapper);

        vo.setTotalBookings((long) bookings.size());
        vo.setConfirmedBookings(bookings.stream().filter(b -> b.getStatus() == 1).count());
        vo.setCompletedBookings(bookings.stream().filter(b -> b.getStatus() == 3).count());
        vo.setCancelledBookings(bookings.stream().filter(b -> b.getStatus() == 4).count());
        vo.setTotalGoodsQuantity(bookings.stream()
                .map(BookingOrder::getGoodsQuantity)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        Map<String, Long> statusMap = bookings.stream()
                .collect(Collectors.groupingBy(b -> BookingVO.getStatusDesc(b.getStatus()), Collectors.counting()));
        vo.setBookingsByStatus(statusMap);

        return vo;
    }

    @Override
    public StatisticsVO getBookingStatistics(StatisticsDTO dto) {
        StatisticsVO vo = new StatisticsVO();

        LambdaQueryWrapper<BookingOrder> wrapper = buildBookingWrapper(dto);
        List<BookingOrder> bookings = bookingOrderMapper.selectList(wrapper);

        Map<String, Long> dateMap = bookings.stream()
                .collect(Collectors.groupingBy(
                        b -> b.getBookingDate().toString(),
                        LinkedHashMap::new,
                        Collectors.counting()));
        vo.setBookingsByDate(dateMap);

        Map<String, BigDecimal> goodsMap = bookings.stream()
                .filter(b -> b.getGoodsType() != null && b.getGoodsQuantity() != null)
                .collect(Collectors.groupingBy(
                        BookingOrder::getGoodsType,
                        Collectors.reducing(BigDecimal.ZERO, BookingOrder::getGoodsQuantity, BigDecimal::add)));
        vo.setGoodsByType(goodsMap);

        Map<String, Long> supplierMap = bookings.stream()
                .collect(Collectors.groupingBy(
                        b -> String.valueOf(b.getSupplierId()),
                        Collectors.counting()));
        vo.setTopSuppliers(supplierMap);

        return vo;
    }

    @Override
    public StatisticsVO getSupplierStatistics() {
        StatisticsVO vo = new StatisticsVO();

        List<Supplier> allSuppliers = supplierMapper.selectList(null);
        vo.setTotalSuppliers((long) allSuppliers.size());
        vo.setActiveSuppliers(allSuppliers.stream().filter(s -> s.getStatus() == 1).count());

        LambdaQueryWrapper<SupplierQualification> qWrapper = new LambdaQueryWrapper<>();
        qWrapper.eq(SupplierQualification::getStatus, 2);
        vo.setExpiringQualifications((long) qualificationMapper.selectCount(qWrapper));

        return vo;
    }

    private LambdaQueryWrapper<BookingOrder> buildBookingWrapper(StatisticsDTO dto) {
        LambdaQueryWrapper<BookingOrder> wrapper = new LambdaQueryWrapper<>();
        if (dto != null) {
            wrapper.ge(dto.getStartDate() != null, BookingOrder::getBookingDate, dto.getStartDate());
            wrapper.le(dto.getEndDate() != null, BookingOrder::getBookingDate, dto.getEndDate());
            wrapper.eq(dto.getSupplierId() != null, BookingOrder::getSupplierId, dto.getSupplierId());
            wrapper.eq(dto.getStatus() != null, BookingOrder::getStatus, dto.getStatus());
        }
        return wrapper;
    }
}
