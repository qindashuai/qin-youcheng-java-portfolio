package com.qindashuai.supply.controller;

import com.qindashuai.supply.dto.StatisticsDTO;
import com.qindashuai.supply.service.ReportExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/report")
@RequiredArgsConstructor
public class ReportExportController {

    private final ReportExportService reportExportService;

    @GetMapping("/booking")
    public void exportBooking(StatisticsDTO dto, HttpServletResponse response) {
        reportExportService.exportBookingReport(dto, response);
    }

    @GetMapping("/supplier")
    public void exportSupplier(HttpServletResponse response) {
        reportExportService.exportSupplierReport(response);
    }

    @GetMapping("/qualification")
    public void exportQualification(HttpServletResponse response) {
        reportExportService.exportQualificationReport(response);
    }
}
