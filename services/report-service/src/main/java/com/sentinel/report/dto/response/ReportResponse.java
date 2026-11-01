package com.sentinel.report.dto.response;

import com.sentinel.report.enums.ReportCategory;
import com.sentinel.report.enums.ReportStatus;

import java.time.Instant;

public record ReportResponse(
        String reportId,
        String reporterId,
        String reportedPlayerId,
        ReportCategory category,
        String description,
        ReportStatus status,
        Instant createdAt
) {
}
