package com.qindashuai.supply.service;

import com.qindashuai.supply.dto.StatisticsDTO;
import com.qindashuai.supply.vo.BookingVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface ReportExportService {

    void exportBookingReport(StatisticsDTO dto, HttpServletResponse response);

    void exportSupplierReport(HttpServletResponse response);

    void exportQualificationReport(HttpServletResponse response);
}
