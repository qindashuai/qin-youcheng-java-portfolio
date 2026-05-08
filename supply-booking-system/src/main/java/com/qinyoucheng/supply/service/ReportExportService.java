package com.qinyoucheng.supply.service;

import com.qinyoucheng.supply.dto.StatisticsDTO;
import com.qinyoucheng.supply.vo.BookingVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface ReportExportService {

    void exportBookingReport(StatisticsDTO dto, HttpServletResponse response);

    void exportSupplierReport(HttpServletResponse response);

    void exportQualificationReport(HttpServletResponse response);
}
