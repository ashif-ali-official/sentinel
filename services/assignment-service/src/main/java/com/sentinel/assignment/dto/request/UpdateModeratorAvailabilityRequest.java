package com.sentinel.assignment.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateModeratorAvailabilityRequest(
        @NotNull Boolean available
) {}