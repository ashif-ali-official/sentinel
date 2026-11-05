package com.sentinel.assignment.kafka.event;

public record CaseAssignedPayload(
        String assignmentId,
        String caseId,
        String moderatorId,
        String status,
        String reportedPlayerId
) {}
