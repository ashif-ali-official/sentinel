package com.sentinel.cases.dto.request;

import com.sentinel.cases.enums.CasePriority;
import jakarta.validation.constraints.NotNull;

public record UpdateCasePriorityRequest(

        @NotNull(message = "Priority is required")
        CasePriority priority

) {
}