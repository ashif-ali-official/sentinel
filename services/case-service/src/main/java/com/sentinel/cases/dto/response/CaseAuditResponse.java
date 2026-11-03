package com.sentinel.cases.dto.response;

import java.time.Instant;

public record CaseAuditResponse(
        String auditId,
        String caseId,
        String action,
        String oldValue,
        String newValue,
        String performedBy,
        Instant createdAt
) {
}