package com.sentinel.report.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class PlayerClientConfig {

    @Bean
    public RestClient.Builder playerRestClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public RestClient playerRestClient(RestClient.Builder builder,
                                       @Value("${sentinel.services.player.base-url}") String baseUrl) {

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(Duration.ofSeconds(3));

        return builder
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();

    }

}
