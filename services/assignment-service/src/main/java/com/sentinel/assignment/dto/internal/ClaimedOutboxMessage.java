package com.sentinel.assignment.dto.internal;

public record ClaimedOutboxMessage(
        String id,
        String claimToken,
        String topic,
        String aggregateId,
        String payload
) {}