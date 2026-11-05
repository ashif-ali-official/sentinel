package com.sentinel.report.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sentinel.security.cognito")
public record CognitoProperties(
        String issuerUri,
        String clientId
) {}
