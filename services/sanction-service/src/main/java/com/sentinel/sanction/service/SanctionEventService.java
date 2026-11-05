package com.sentinel.sanction.service;

import com.sentinel.sanction.dto.event.ModerationDecisionMadeEvent;
import com.sentinel.sanction.entity.ProcessedEvent;
import com.sentinel.sanction.entity.SanctionReview;
import com.sentinel.sanction.repository.ProcessedEventRepository;
import com.sentinel.sanction.repository.SanctionReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SanctionEventService {
    private final SanctionReviewRepository reviewRepository;
    private final ProcessedEventRepository processedEventRepository;

    @Transactional
    public void process(ModerationDecisionMadeEvent event) {
        if (event == null || event.eventId() == null || event.payload() == null
                || !"MODERATION_DECISION_MADE".equals(event.eventType()) || event.eventVersion() != 1) {
            throw new IllegalArgumentException("Invalid or unsupported moderation decision event");
        }
        if (processedEventRepository.existsById(event.eventId())) return;
        ModerationDecisionMadeEvent.Payload payload = event.payload();
        if (payload.caseId() == null || payload.assignmentId() == null || payload.moderatorId() == null
                || payload.reportedPlayerId() == null || payload.decision() == null || payload.decidedAt() == null) {
            throw new IllegalArgumentException("Moderation decision payload is incomplete");
        }
        if (event.aggregateId() != null && !event.aggregateId().equals(payload.caseId())) {
            throw new IllegalArgumentException("Event aggregate ID does not match case ID");
        }
        if (reviewRepository.existsById(payload.caseId())) {
            throw new IllegalStateException("A sanction review already exists for case " + payload.caseId());
        }
        reviewRepository.save(new SanctionReview(payload.caseId(), payload.assignmentId(), payload.reportedPlayerId(),
                payload.moderatorId(), payload.decision(), payload.notes(), payload.decidedAt()));
        processedEventRepository.save(new ProcessedEvent(event.eventId(), event.eventType(), payload.caseId()));
    }
}
