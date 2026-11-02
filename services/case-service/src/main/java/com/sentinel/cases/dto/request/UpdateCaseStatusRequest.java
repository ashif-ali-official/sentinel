package com.sentinel.cases.dto.request;

import com.sentinel.cases.enums.CaseStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateCaseStatusRequest(

        @NotNull(message = "Status is required")
        CaseStatus status

) {
}