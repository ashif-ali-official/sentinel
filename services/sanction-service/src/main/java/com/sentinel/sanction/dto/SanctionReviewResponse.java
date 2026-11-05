package com.sentinel.sanction.dto;

import com.sentinel.sanction.entity.SanctionReview;
import com.sentinel.sanction.entity.enums.SanctionStatus;
import com.sentinel.sanction.entity.enums.SanctionType;
import java.time.Instant;

public record SanctionReviewResponse(String caseId, String assignmentId, String reportedPlayerId,
        String moderatorId, String moderationDecision, String decisionNotes, SanctionStatus status,
        SanctionType sanctionType, Long durationSeconds, String actionedBy, Instant decidedAt,
        Instant actionedAt, Long version) {
    public static SanctionReviewResponse from(SanctionReview review) {
        return new SanctionReviewResponse(review.getCaseId(), review.getAssignmentId(), review.getReportedPlayerId(),
                review.getModeratorId(), review.getModerationDecision(), review.getDecisionNotes(), review.getStatus(),
                review.getSanctionType(), review.getDurationSeconds(), review.getActionedBy(), review.getDecidedAt(),
                review.getActionedAt(), review.getVersion());
    }
}
