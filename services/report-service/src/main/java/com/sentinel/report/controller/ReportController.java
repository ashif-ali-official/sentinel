package com.sentinel.report.controller;

import com.sentinel.report.dto.request.CreateReportRequest;
import com.sentinel.report.dto.response.PageResponse;
import com.sentinel.report.dto.response.ReportResponse;
import com.sentinel.report.enums.ReportCategory;
import com.sentinel.report.enums.ReportStatus;
import com.sentinel.report.service.ReportService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
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

    @GetMapping("/{reportId}")
    @ResponseStatus(HttpStatus.OK)
    public ReportResponse getReport( @PathVariable String reportId) {
        return reportService.getReportById(reportId);
    }

    @GetMapping
    public PageResponse<ReportResponse> getReports(

            @RequestParam(required = false)
            ReportStatus status,

            @RequestParam(required = false)
            ReportCategory category,

            @RequestParam(required = false)
            String reportedPlayerId,

            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,

            @RequestParam(defaultValue = "10")
            @Min(1)
            @Max(100)
            int size
    ) {
        return reportService.getReports(
                status,
                category,
                reportedPlayerId,
                page,
                size
        );
    }

}