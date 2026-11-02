package com.sentinel.report.service;

import com.sentinel.report.dto.request.CreateReportRequest;
import com.sentinel.report.dto.response.ReportResponse;
import com.sentinel.report.enums.ReportCategory;
import com.sentinel.report.enums.ReportStatus;
import com.sentinel.report.entity.Report;
import com.sentinel.report.repository.ReportRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportService reportService;

    @Test
    void shouldCreateReportSuccessfully() {

        CreateReportRequest request = new CreateReportRequest(
                "PLAYER-101",
                "PLAYER-202",
                ReportCategory.HARASSMENT,
                "Abusive messages"
        );

        when(reportRepository.save(any(Report.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReportResponse response = reportService.createReport(request);

        assertNotNull(response.reportId());
        assertEquals("PLAYER-101", response.reporterId());
        assertEquals("PLAYER-202", response.reportedPlayerId());
        assertEquals(ReportCategory.HARASSMENT, response.category());
        assertEquals(ReportStatus.SUBMITTED, response.status());

        verify(reportRepository, times(1)).save(any(Report.class));
    }
}