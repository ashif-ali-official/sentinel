package com.sentinel.assignment.kafka.event;

public record CaseCreatedPayload(
        String caseId,
        String reportId,
        String reporterId,
        String reportedPlayerId,
        String category,
        String status
) {}