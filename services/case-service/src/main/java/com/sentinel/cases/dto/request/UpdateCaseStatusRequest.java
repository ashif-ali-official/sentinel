package com.sentinel.cases.dto.request;

import com.sentinel.cases.enums.CaseStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateCaseStatusRequest(

        @NotNull(message = "Status is required")
        CaseStatus status,

        @NotNull
        @PositiveOrZero
        Long version

) {
}