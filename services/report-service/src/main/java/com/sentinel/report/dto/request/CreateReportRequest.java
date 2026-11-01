package com.sentinel.report.dto.request;

import com.sentinel.report.enums.ReportCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReportRequest(
    @NotBlank(message = "Reporter ID is required")
    String reporterId,

    @NotBlank(message = "Reported player ID is required")
    String reportedPlayerId,

    @NotNull(message = "Report category is required")
    ReportCategory category,

    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    String description
) {}
