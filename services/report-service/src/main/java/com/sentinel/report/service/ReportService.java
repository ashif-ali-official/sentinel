package com.sentinel.report.service;

import com.sentinel.report.client.PlayerClient;
import com.sentinel.report.dto.request.CreateReportRequest;
import com.sentinel.report.dto.response.PageResponse;
import com.sentinel.report.dto.response.ReportResponse;
import com.sentinel.report.entity.Report;
import com.sentinel.report.enums.ReportCategory;
import com.sentinel.report.enums.ReportStatus;
import com.sentinel.report.event.ReportCreatedEvent;
import com.sentinel.report.exception.InvalidPlayerException;
import com.sentinel.report.exception.ReportNotFoundException;
import com.sentinel.report.repository.OutboxRepository;
import com.sentinel.report.repository.ReportRepository;
import com.sentinel.report.repository.ReportSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final OutboxService outboxService;
    private final PlayerClient playerClient;

    @Transactional
    public ReportResponse createReport(CreateReportRequest request) {

        Report report = new Report(
                request.reporterId(),
                request.reportedPlayerId(),
                request.category(),
                request.description()
        );

        if (!playerClient.playerExists(request.reporterId())) {
            throw new InvalidPlayerException(
                    "Reporter does not exist"
            );
        }

        if (!playerClient.playerExists(request.reportedPlayerId())) {
            throw new InvalidPlayerException(
                    "Reported player does not exist"
            );
        }

        Report savedReport = reportRepository.save(report);

        ReportCreatedEvent event = new ReportCreatedEvent(
                UUID.randomUUID(),
                "ReportCreated",
                1,
                savedReport.getId(),
                UUID.randomUUID().toString(),
                Instant.now(),
                "report-service",
                new ReportCreatedEvent.ReportPayload(
                        savedReport.getId(),
                        savedReport.getReporterId(),
                        savedReport.getReportedPlayerId(),
                        savedReport.getDescription(),
                        savedReport.getCategory().name(),
                        savedReport.getStatus().name()
                )
        );

        outboxService.saveEvent(
                "REPORT",
                savedReport.getId(),
                "ReportCreated",
                event
        );

        return mapToReportResponse(savedReport);
    }

    @Transactional(readOnly = true)
    public ReportResponse getReportById(String reportId) {
        Report report = reportRepository.findById(reportId).orElseThrow(() -> new ReportNotFoundException(reportId));
        return mapToReportResponse(report);
    }

    @Transactional(readOnly = true)
    public PageResponse<ReportResponse> getReports(
            ReportStatus status,
            ReportCategory category,
            String reportedPlayerId,
            int page,
            int size
    ) {

        Specification<Report> specification =
                Specification.where(ReportSpecification.hasStatus(status))
                        .and(ReportSpecification.hasCategory(category))
                        .and(ReportSpecification.reportedPlayerIs(reportedPlayerId));

        org.springframework.data.domain.Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Report> reports =
                reportRepository.findAll(specification, pageable);

        List<ReportResponse> content = reports.getContent()
                .stream()
                .map(this::mapToReportResponse)
                .toList();

        return new PageResponse<>(
                content,
                reports.getNumber(),
                reports.getSize(),
                reports.getTotalElements(),
                reports.getTotalPages(),
                reports.isLast()
        );
    }

    private ReportResponse mapToReportResponse(Report report) {
        return new ReportResponse(
                report.getId(),
                report.getReporterId(),
                report.getReportedPlayerId(),
                report.getCategory(),
                report.getDescription(),
                report.getStatus(),
                report.getCreatedAt()
        );
    }

}
