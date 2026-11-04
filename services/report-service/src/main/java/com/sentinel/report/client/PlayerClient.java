package com.sentinel.report.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class PlayerClient {

    private final RestClient playerRestClient;


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

}
