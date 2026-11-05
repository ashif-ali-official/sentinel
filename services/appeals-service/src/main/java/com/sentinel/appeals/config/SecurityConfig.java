package com.sentinel.appeals.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.stream.Collectors;

@EnableConfigurationProperties(CognitoProperties.class)
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http, JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
        http
                .csrf( csrf -> csrf.disable() )
                .authorizeHttpRequests( auth -> auth
                        .requestMatchers("/actuator/health")
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt( jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
                );
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder(CognitoProperties properties) {
        String issuer = properties.issuerUri();
        String clientId = properties.clientId();

        NimbusJwtDecoder decoder = JwtDecoders.fromIssuerLocation(issuer);
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
        OAuth2TokenValidator<Jwt> cognitoValidator = jwt -> {
            if(!"access".equals(jwt.getClaimAsString("token_use"))) {
                return OAuth2TokenValidatorResult.failure(
                        new OAuth2Error("invalid_token",
                                "Only access tokens are accepted", null));
            }

            if (!clientId.equals(jwt.getClaimAsString("client_id"))) {
                return OAuth2TokenValidatorResult.failure(
                        new OAuth2Error("invalid_token",
                                "Invalid Cognito app client", null));
            }
            return OAuth2TokenValidatorResult.success();
        };

        decoder.setJwtValidator(
                new DelegatingOAuth2TokenValidator<>(
                        withIssuer,
                        cognitoValidator
                )
        );

        return decoder;

    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {

            List<String> groups =
                    jwt.getClaimAsStringList("cognito:groups");

            if (groups == null) {
                return List.of();
            }

            return groups.stream()
                    .map(group -> new SimpleGrantedAuthority("ROLE_" + group))
                    .collect(Collectors.toList());
        });

        return converter;
    }

}
