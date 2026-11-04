package com.sentinel.cases.service;

import com.sentinel.cases.dto.event.CaseCreatedEvent;
import com.sentinel.cases.entity.CaseOutboxEvent;
import com.sentinel.cases.entity.ModerationCase;
import com.sentinel.cases.repository.CaseOutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.json.JsonParseException;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaseOutboxService {

    private static final String CASE_CREATED_TOPIC = "sentinel.case.created";

    private final CaseOutboxEventRepository caseOutboxEventRepository;
    private final ObjectMapper objectMapper;

    public void publishCaseCreated(ModerationCase moderationCase) {
        try{
            String eventId = UUID.randomUUID().toString();
            CaseCreatedEvent caseCreatedEvent = new CaseCreatedEvent(
                    eventId,
                    "CASE_CREATED",
                    1,
                    moderationCase.getId(),
                    null,
                    Instant.now(),
                    "case-service",
                    new CaseCreatedEvent.Payload(
                            moderationCase.getId(),
                            moderationCase.getReportId(),
                            moderationCase.getReporterId(),
                            moderationCase.getReportedPlayerId(),
                            moderationCase.getCategory().name(),
                            moderationCase.getStatus().name()
                    )
            );

            String payload = objectMapper.writeValueAsString(caseCreatedEvent);
            CaseOutboxEvent caseOutboxEvent = new CaseOutboxEvent(
                    "CASE",
                    moderationCase.getId(),
                    "CASE_CREATED",
                    CASE_CREATED_TOPIC,
                    payload
            );

            caseOutboxEventRepository.save(caseOutboxEvent);

        } catch (JsonParseException e) {
            throw new IllegalStateException(
                    "Failed to serialize CASE_CREATED event", e
            );
        }
    }

}
