package com.sentinel.sanction.service;

import com.sentinel.sanction.dto.ApplySanctionRequest;
import com.sentinel.sanction.dto.SanctionReviewResponse;
import com.sentinel.sanction.dto.event.SanctionAppliedEvent;
import com.sentinel.sanction.entity.SanctionOutboxEvent;
import com.sentinel.sanction.entity.SanctionReview;
import com.sentinel.sanction.repository.SanctionOutboxRepository;
import com.sentinel.sanction.repository.SanctionReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SanctionService {
    private final SanctionReviewRepository reviewRepository;
    private final SanctionOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public SanctionReviewResponse get(String caseId) { return SanctionReviewResponse.from(find(caseId)); }

    @Transactional
    public SanctionReviewResponse apply(String caseId, ApplySanctionRequest request) {
        SanctionReview review = find(caseId);
        if (!review.getVersion().equals(request.expectedVersion())) throw new ObjectOptimisticLockingFailureException(SanctionReview.class, caseId);
        review.apply(request.sanctionType(), request.durationSeconds(), request.actionedBy());
        SanctionReview saved = reviewRepository.saveAndFlush(review);
        String eventId = UUID.randomUUID().toString();
        SanctionAppliedEvent event = new SanctionAppliedEvent(eventId, "SANCTION_APPLIED", 1, caseId,
                Instant.now(), "sanction-service", new SanctionAppliedEvent.Payload(saved.getCaseId(),
                saved.getAssignmentId(), saved.getReportedPlayerId(), saved.getSanctionType(),
                saved.getDurationSeconds(), saved.getActionedBy(), saved.getActionedAt()));
        try {
            outboxRepository.save(new SanctionOutboxEvent(eventId, caseId, event.eventType(),
                    "sentinel.sanction.applied", objectMapper.writeValueAsString(event)));
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to serialize sanction event", exception);
        }
        return SanctionReviewResponse.from(saved);
    }

    private SanctionReview find(String caseId) { return reviewRepository.findById(caseId).orElseThrow(() -> new SanctionNotFoundException(caseId)); }
}
