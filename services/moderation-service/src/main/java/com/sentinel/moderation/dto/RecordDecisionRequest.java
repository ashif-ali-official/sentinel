package com.sentinel.moderation.dto;

import com.sentinel.moderation.entity.enums.ModerationDecision;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record RecordDecisionRequest(
        @NotBlank String moderatorId,
        @NotNull ModerationDecision decision,
        @Size(max = 2000) String notes,
        @NotNull @PositiveOrZero Long expectedVersion
) {
}
