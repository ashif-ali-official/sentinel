package com.sentinel.sanction.dto;

import com.sentinel.sanction.entity.enums.SanctionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ApplySanctionRequest(
        @NotNull SanctionType sanctionType,
        @PositiveOrZero Long durationSeconds,
        @NotBlank @Size(max = 100) String actionedBy,
        @NotNull @PositiveOrZero Long expectedVersion
) {}
