package com.sentinel.assignment.client;

import com.sentinel.assignment.dto.response.PlayerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class PlayerClient {

    private final RestClient playerRestClient;

    public PlayerResponse getPlayer(String playerId) {

        return playerRestClient.get()
                .uri("/api/v1/players/{playerId}", playerId)
                .retrieve()
                .body(PlayerResponse.class);
    }
}