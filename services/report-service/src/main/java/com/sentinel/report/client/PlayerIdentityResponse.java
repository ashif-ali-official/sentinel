package com.sentinel.report.client;

public record PlayerIdentityResponse(
        String playerId,
        String username,
        String displayName,
        String email,
        String region,
        String status,
        String createdAt,
        String updatedAt
) {
}
