package com.sentinel.assignment.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateAssignmentRequest(
        @NotBlank String caseId,
        @NotBlank String region
) {}