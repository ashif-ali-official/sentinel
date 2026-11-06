package com.sentinel.report.client;

import com.sentinel.report.exception.PlayerServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.http.HttpHeaders;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlayerClient {

    private final RestClient playerRestClient;

    @Retry(name="playerService", fallbackMethod = "playerExistsFallback")
    @CircuitBreaker(name="playerService", fallbackMethod = "playerExistsFallback")
    public boolean playerExists(String playerId) {
        try{
            playerRestClient.get()
                    .uri("/api/v1/players/{playerId}", playerId)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        }
    }

    public PlayerIdentityResponse getCurrentPlayer(String accessToken, String identityToken) {
        return playerRestClient.get()
                .uri("/api/v1/players/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header("X-Identity-Token", identityToken)
                .retrieve()
                .body(PlayerIdentityResponse.class);
    }

    public boolean playerExistsFallback(String playerId, Exception exception){
        log.error("Player service unavailable while validating player {}", playerId, exception);

        throw new PlayerServiceUnavailableException(
                "Unable to validate player. Please try again later"
        );
    }

}
