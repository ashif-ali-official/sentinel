package com.sentinel.report.service;

import com.sentinel.report.dto.request.CreateReportRequest;
import com.sentinel.report.dto.response.ReportResponse;
import com.sentinel.report.entity.Report;
import com.sentinel.report.repository.ReportRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Transactional
    public ReportResponse createReport(CreateReportRequest request) {

        Report report = new Report(
                request.reporterId(),
                request.reportedPlayerId(),
                request.category(),
                request.description()
        );

        Report savedReport = reportRepository.save(report);

        return new ReportResponse(
                savedReport.getId(),
                savedReport.getReporterId(),
                savedReport.getReportedPlayerId(),
                savedReport.getCategory(),
                savedReport.getDescription(),
                savedReport.getStatus(),
                savedReport.getCreatedAt()
        );
    }

}
