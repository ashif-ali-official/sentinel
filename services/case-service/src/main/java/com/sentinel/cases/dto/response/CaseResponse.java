package com.sentinel.cases.dto.response;

import com.sentinel.cases.enums.CasePriority;
import com.sentinel.cases.enums.CaseStatus;
import com.sentinel.cases.enums.ReportCategory;

import java.time.Instant;

public record CaseResponse(
        String caseId,
        String reportId,
        String reporterId,
        String reportedPlayerId,
        String description,
        ReportCategory category,
        CaseStatus status,
        CasePriority priority,
        Instant createdAt,
        Instant updatedAt
) {
}