package com.sentinel.assignment.dto.response;

public record PlayerResponse(
        String id,
        String username,
        String displayName,
        String region,
        String status
) {}