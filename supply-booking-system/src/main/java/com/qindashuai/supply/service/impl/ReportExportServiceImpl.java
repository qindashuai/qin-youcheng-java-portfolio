package com.qindashuai.supply.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qindashuai.supply.dto.StatisticsDTO;
import com.qindashuai.supply.entity.BookingOrder;
import com.qindashuai.supply.entity.Supplier;
import com.qindashuai.supply.entity.SupplierQualification;
import com.qindashuai.supply.mapper.BookingOrderMapper;
import com.qindashuai.supply.mapper.SupplierMapper;
import com.qindashuai.supply.mapper.SupplierQualificationMapper;
import com.qindashuai.supply.service.ReportExportService;
import com.qindashuai.supply.util.ExcelExportUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportExportServiceImpl implements ReportExportService {

    private final BookingOrderMapper bookingOrderMapper;
    private final SupplierMapper supplierMapper;
    private final SupplierQualificationMapper qualificationMapper;

    @Override
    public void exportBookingReport(StatisticsDTO dto, HttpServletResponse response) {
        LambdaQueryWrapper<BookingOrder> wrapper = new LambdaQueryWrapper<>();
        if (dto != null) {
            wrapper.ge(dto.getStartDate() != null, BookingOrder::getBookingDate, dto.getStartDate());
            wrapper.le(dto.getEndDate() != null, BookingOrder::getBookingDate, dto.getEndDate());
            wrapper.eq(dto.getSupplierId() != null, BookingOrder::getSupplierId, dto.getSupplierId());
            wrapper.eq(dto.getStatus() != null, BookingOrder::getStatus, dto.getStatus());
        }
        wrapper.orderByDesc(BookingOrder::getCreateTime);
        List<BookingOrder> bookings = bookingOrderMapper.selectList(wrapper);

        String[] headers = {"预约单号", "供应商ID", "预约日期", "预约时段", "车牌号", "司机姓名",
                "货物类型", "货物数量", "状态", "创建时间"};
        String[] fields = {"bookingNo", "supplierId", "bookingDate", "bookingTime", "vehicleNo",
                "driverName", "goodsType", "goodsQuantity", "status", "createTime"};

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (BookingOrder booking : bookings) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("bookingNo", booking.getBookingNo());
            row.put("supplierId", booking.getSupplierId());
            row.put("bookingDate", booking.getBookingDate() != null ? booking.getBookingDate().toString() : "");
            row.put("bookingTime", booking.getBookingTime());
            row.put("vehicleNo", booking.getVehicleNo());
            row.put("driverName", booking.getDriverName());
            row.put("goodsType", booking.getGoodsType());
            row.put("goodsQuantity", booking.getGoodsQuantity());
            row.put("status", getStatusDesc(booking.getStatus()));
            row.put("createTime", booking.getCreateTime() != null ? booking.getCreateTime().toString() : "");
            dataList.add(row);
        }

        ExcelExportUtil.export(response, "预约订单报表", headers, fields, dataList);
    }

    @Override
    public void exportSupplierReport(HttpServletResponse response) {
        List<Supplier> suppliers = supplierMapper.selectList(null);

        String[] headers = {"供应商编码", "供应商名称", "联系人", "联系电话", "经营范围", "地址", "状态"};
        String[] fields = {"supplierCode", "supplierName", "contactPerson", "contactPhone",
                "businessScope", "address", "status"};

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Supplier supplier : suppliers) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("supplierCode", supplier.getSupplierCode());
            row.put("supplierName", supplier.getSupplierName());
            row.put("contactPerson", supplier.getContactPerson());
            row.put("contactPhone", supplier.getContactPhone());
            row.put("businessScope", supplier.getBusinessScope());
            row.put("address", supplier.getAddress());
            row.put("status", supplier.getStatus() == 1 ? "启用" : "禁用");
            dataList.add(row);
        }

        ExcelExportUtil.export(response, "供应商信息报表", headers, fields, dataList);
    }

    @Override
    public void exportQualificationReport(HttpServletResponse response) {
        List<SupplierQualification> qualifications = qualificationMapper.selectList(null);

        String[] headers = {"供应商ID", "资质类型", "资质名称", "证书编号", "发证日期", "到期日期", "状态"};
        String[] fields = {"supplierId", "qualificationType", "qualificationName", "certificateNo",
                "issueDate", "expireDate", "status"};

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (SupplierQualification q : qualifications) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("supplierId", q.getSupplierId());
            row.put("qualificationType", q.getQualificationType());
            row.put("qualificationName", q.getQualificationName());
            row.put("certificateNo", q.getCertificateNo());
            row.put("issueDate", q.getIssueDate() != null ? q.getIssueDate().toString() : "");
            row.put("expireDate", q.getExpireDate() != null ? q.getExpireDate().toString() : "");
            row.put("status", getQualificationStatusDesc(q.getStatus()));
            dataList.add(row);
        }

        ExcelExportUtil.export(response, "资质信息报表", headers, fields, dataList);
    }

    private String getStatusDesc(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "待确认";
            case 1: return "已确认";
            case 2: return "已入园";
            case 3: return "已完成";
            case 4: return "已取消";
            default: return "未知";
        }
    }

    private String getQualificationStatusDesc(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "失效";
            case 1: return "有效";
            case 2: return "即将过期";
            default: return "未知";
        }
    }
}
