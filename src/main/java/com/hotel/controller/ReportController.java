package com.hotel.controller;

import com.hotel.dto.DailyReportResponse;
import com.hotel.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/daily")
    public ResponseEntity<DailyReportResponse> getDailyReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        DailyReportResponse report = reportService.getDailyReport(date);
        return ResponseEntity.ok(report);
    }
}

