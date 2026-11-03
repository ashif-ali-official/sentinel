package com.sentinel.cases.dto.request;

import com.sentinel.cases.enums.CasePriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateCasePriorityRequest(

        @NotNull(message = "Priority is required")
        CasePriority priority,

        @NotNull
        @PositiveOrZero
        Long version,

        @NotBlank
        String performedBy


) {
}