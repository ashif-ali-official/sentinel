package com.sentinel.moderation.dto;

import com.sentinel.moderation.entity.ModerationWorkItem;
import com.sentinel.moderation.entity.enums.WorkItemStatus;
import com.sentinel.moderation.entity.enums.ModerationDecision;

import java.time.Instant;

public record ModerationWorkItemResponse(
        String caseId,
        String assignmentId,
        String moderatorId,
        WorkItemStatus status,
        Instant assignedAt,
        Instant startedAt,
        ModerationDecision decision,
        String decisionNotes,
        Instant decidedAt,
        Long version
) {
    public static ModerationWorkItemResponse from(ModerationWorkItem item) {
        return new ModerationWorkItemResponse(item.getCaseId(), item.getAssignmentId(), item.getModeratorId(),
                item.getStatus(), item.getAssignedAt(), item.getStartedAt(), item.getDecision(),
                item.getDecisionNotes(), item.getDecidedAt(), item.getVersion());
    }
}
