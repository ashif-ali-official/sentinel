package com.sentinel.appeals.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
public record DecideAppealRequest(@NotNull Boolean accepted,@NotBlank @Size(max=100) String decidedBy,
        @Size(max=2000) String decisionNotes,@NotNull @PositiveOrZero Long expectedVersion){}
