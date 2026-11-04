package com.sentinel.assignment.dto.response;

import java.time.LocalDateTime;

public record ModeratorResponse(
        String id,
        String username,
        String displayName,
        String region,
        boolean active,
        boolean available,
        int maxActiveCases,
        long currentActiveCases,
        LocalDateTime createdAt
) {}