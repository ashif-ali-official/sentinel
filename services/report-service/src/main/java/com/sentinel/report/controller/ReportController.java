package com.sentinel.report.controller;

import com.sentinel.report.dto.request.CreateReportRequest;
import com.sentinel.report.dto.response.ReportResponse;
import com.sentinel.report.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReportResponse createReport(
            @Valid @RequestBody CreateReportRequest request
    ) {
        return reportService.createReport(request);
    }
}