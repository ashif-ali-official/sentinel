package com.sentinel.cases.service;

import com.sentinel.cases.dto.event.ReportCreatedEvent;
import com.sentinel.cases.entity.ModerationCase;
import com.sentinel.cases.entity.ProcessedEvent;
import com.sentinel.cases.enums.ReportCategory;
import com.sentinel.cases.repository.CaseRepository;
import com.sentinel.cases.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CaseEventConsumerService {

    private final CaseRepository caseRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final CaseOutboxService caseOutboxService;

    @Transactional
    public void processReportCreated(ReportCreatedEvent reportCreatedEvent) {
        validateEvent(reportCreatedEvent);

        if(processedEventRepository.existsById(reportCreatedEvent.eventId())) {
            log.info("Duplicate event, ignored: {}", reportCreatedEvent.eventId());
            return;
        }

        ReportCreatedEvent.ReportPayload payload =  reportCreatedEvent.payload();
        ReportCategory category = ReportCategory.valueOf(payload.category());

        ModerationCase moderationCase = new ModerationCase(
                payload.reportId(),
                payload.reporterId(),
                payload.reportedPlayerId(),
                payload.description(),
                category
        );

        ModerationCase savedCase = caseRepository.save(moderationCase);

        ProcessedEvent processedEvent = new ProcessedEvent(
            reportCreatedEvent.eventId(),
            reportCreatedEvent.eventType(),
            reportCreatedEvent.aggregateId()
        );

        processedEventRepository.save(processedEvent);
        log.info(
                "Moderation case created. Case ID: {}, Report ID: {}",
                moderationCase.getId(),
                moderationCase.getReportId()
        );

        caseOutboxService.publishCaseCreated(moderationCase);
        log.info(
                "Moderation case sent to CaseOutbox. Case ID: {}, Report ID: {}",
                moderationCase.getId(),
                moderationCase.getReportId()
        );

    }

    private void validateEvent(ReportCreatedEvent reportCreatedEvent) {
        if(reportCreatedEvent == null
            || reportCreatedEvent.eventId() == null
            || reportCreatedEvent.eventType() == null
            || reportCreatedEvent.payload() == null) {
            throw new IllegalArgumentException("Invalid ReportCreated event");
        }

        if(!"ReportCreated".equals(reportCreatedEvent.eventType())) {
            throw new IllegalArgumentException("Unsupported event type: " +  reportCreatedEvent.eventType());
        }

        if(reportCreatedEvent.eventVersion() != 1) {
            throw new IllegalArgumentException("Unsupported event version: " +  reportCreatedEvent.eventVersion());
        }

        ReportCreatedEvent.ReportPayload payload = reportCreatedEvent.payload();

        if (payload.reportId() == null
                || payload.reporterId() == null
                || payload.reportedPlayerId() == null
                || payload.description() == null
                || payload.category() == null) {
            throw new IllegalArgumentException(
                    "ReportCreated event contains incomplete payload"
            );
        }

    }

}
