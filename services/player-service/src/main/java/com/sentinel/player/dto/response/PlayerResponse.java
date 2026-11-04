package com.sentinel.player.dto.response;

import com.sentinel.player.entity.enums.PlayerStatus;

import java.time.LocalDateTime;

public record PlayerResponse(
        String playerId,
        String username,
        String displayName,
        String email,
        String region,
        PlayerStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}