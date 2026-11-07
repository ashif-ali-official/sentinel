package com.sentinel.report.service;

import com.sentinel.report.client.PlayerClient;
import com.sentinel.report.client.PlayerIdentityResponse;
import com.sentinel.report.dto.request.CreateReportRequest;
import com.sentinel.report.entity.Report;
import com.sentinel.report.enums.ReportCategory;
import com.sentinel.report.enums.ReportStatus;
import com.sentinel.report.exception.InvalidPlayerException;
import com.sentinel.report.exception.ReportNotFoundException;
import com.sentinel.report.repository.OutboxRepository;
import com.sentinel.report.repository.ReportRepository;
import com.sentinel.report.repository.ReportSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock private ReportRepository reportRepository;
    @Mock private OutboxService outboxService;
    @Mock private PlayerClient playerClient;
    @InjectMocks private ReportService reportService;

    @Test
    void createsReportAndOutboxEventAfterBothPlayersAreValidated() {
        CreateReportRequest request = request();
        when(playerClient.playerExists("PLAYER-101")).thenReturn(true);
        when(playerClient.playerExists("PLAYER-202")).thenReturn(true);
        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = reportService.createReport(request);

        assertNotNull(response.reportId());
        assertEquals("PLAYER-101", response.reporterId());
        assertEquals("PLAYER-202", response.reportedPlayerId());
        assertEquals(ReportCategory.HARASSMENT, response.category());
        assertEquals(ReportStatus.SUBMITTED, response.status());
        ArgumentCaptor<Object> event = ArgumentCaptor.forClass(Object.class);
        verify(outboxService).saveEvent(eq("REPORT"), eq(response.reportId()), eq("ReportCreated"), event.capture());
        assertNotNull(event.getValue());
    }

    @Test
    void doesNotPersistWhenReporterDoesNotExist() {
        when(playerClient.playerExists("PLAYER-101")).thenReturn(false);

        assertThrows(InvalidPlayerException.class, () -> reportService.createReport(request()));

        verify(playerClient, never()).playerExists("PLAYER-202");
        verifyNoInteractions(reportRepository, outboxService);
    }

    @Test
    void doesNotPersistWhenReportedPlayerDoesNotExist() {
        when(playerClient.playerExists("PLAYER-101")).thenReturn(true);
        when(playerClient.playerExists("PLAYER-202")).thenReturn(false);

        assertThrows(InvalidPlayerException.class, () -> reportService.createReport(request()));

        verifyNoInteractions(reportRepository, outboxService);
    }

    @Test
    void findsReportAndThrowsWhenMissing() {
        Report report = report();
        when(reportRepository.findById(report.getId())).thenReturn(Optional.of(report));
        when(reportRepository.findById("missing")).thenReturn(Optional.empty());

        assertEquals(report.getId(), reportService.getReportById(report.getId()).reportId());
        assertThrows(ReportNotFoundException.class, () -> reportService.getReportById("missing"));
    }

    @Test
    void returnsFilteredReportPage() {
        Report report = report();
        when(reportRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(report), PageRequest.of(0, 10), 1));

        var page = reportService.getReports(ReportStatus.SUBMITTED, ReportCategory.HARASSMENT, "PLAYER-202", 0, 10);

        assertEquals(1, page.totalElements());
        assertEquals(report.getId(), page.content().getFirst().reportId());
        verify(reportRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void returnsReportsWherePlayerWasEitherReporterOrReported() {
        Report report = report();
        when(reportRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(report), PageRequest.of(0, 20), 1));

        var page = reportService.getReportsForPlayer("PLAYER-101", 0, 20);

        assertEquals(1, page.totalElements());
        assertEquals("PLAYER-101", page.content().getFirst().reporterId());
    }

    @Test
    void resolvesSignedInPlayerBeforeLoadingTheirReports() {
        Report report = report();
        when(playerClient.getCurrentPlayer("access", "identity"))
                .thenReturn(new PlayerIdentityResponse("PLAYER-101", "player_1", "Player One", "p1@example.com", "APAC", "ACTIVE", null, null));
        when(reportRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(report), PageRequest.of(0, 10), 1));

        var result = reportService.getMyReports("access", "identity", 0, 10);

        assertEquals(1, result.totalElements());
        verify(playerClient).getCurrentPlayer("access", "identity");
    }

    @Test
    void rejectsMissingPlayerIdentity() {
        when(playerClient.getCurrentPlayer("access", "identity")).thenReturn(null);
        assertThrows(IllegalStateException.class,
                () -> reportService.getMyReports("access", "identity", 0, 10));

        when(playerClient.getCurrentPlayer("access", "identity"))
                .thenReturn(new PlayerIdentityResponse(" ", "player_1", "Player One", "p1@example.com", "APAC", "ACTIVE", null, null));
        assertThrows(IllegalStateException.class,
                () -> reportService.getMyReports("access", "identity", 0, 10));
    }

    @Test
    void reportSpecificationCreatesOptionalPredicates() {
        assertNull(ReportSpecification.hasStatus(null).toPredicate(null, null, null));
        assertNull(ReportSpecification.hasCategory(null).toPredicate(null, null, null));
        assertNull(ReportSpecification.reportedPlayerIs(null).toPredicate(null, null, null));
        assertNull(ReportSpecification.involvesPlayer(null).toPredicate(null, null, null));
    }

    private CreateReportRequest request() {
        return new CreateReportRequest("PLAYER-101", "PLAYER-202", ReportCategory.HARASSMENT, "Abusive messages");
    }

    private Report report() {
        return new Report("PLAYER-101", "PLAYER-202", ReportCategory.HARASSMENT, "Abusive messages");
    }
}
