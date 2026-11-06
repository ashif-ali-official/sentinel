package com.sentinel.player.controller;

import com.sentinel.player.dto.request.CreatePlayerRequest;
import com.sentinel.player.dto.request.UpdatePlayerRequest;
import com.sentinel.player.dto.response.PageResponse;
import com.sentinel.player.dto.response.PlayerResponse;
import com.sentinel.player.entity.enums.PlayerStatus;
import com.sentinel.player.service.PlayerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/players")
@Validated
public class PlayerController {

    private final PlayerService playerService;
    private final JwtDecoder idTokenDecoder;

    public PlayerController(
            PlayerService playerService,
            @Qualifier("cognitoIdTokenDecoder") JwtDecoder idTokenDecoder
    ) {
        this.playerService = playerService;
        this.idTokenDecoder = idTokenDecoder;
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PlayerResponse> getCurrentPlayer(
            @AuthenticationPrincipal Jwt accessToken,
            @RequestHeader("X-Identity-Token") String identityToken
    ) {
        final Jwt decodedIdentity;
        try {
            decodedIdentity = idTokenDecoder.decode(identityToken);
        } catch (JwtException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid identity token", exception);
        }

        if (!Objects.equals(accessToken.getSubject(), decodedIdentity.getSubject())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identity token does not match access token");
        }

        var groups = decodedIdentity.getClaimAsStringList("cognito:groups");
        if (groups == null || !groups.contains("PLAYER")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "A PLAYER account is required");
        }

        String email = decodedIdentity.getClaimAsString("email");
        Object emailVerifiedClaim = decodedIdentity.getClaims().get("email_verified");
        boolean emailVerified = Boolean.TRUE.equals(emailVerifiedClaim)
                || "true".equalsIgnoreCase(String.valueOf(emailVerifiedClaim));
        if (email == null || email.isBlank() || !emailVerified) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "A verified email address is required");
        }

        return ResponseEntity.ok(playerService.getPlayerByEmail(email));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    public ResponseEntity<PlayerResponse> createPlayer(
            @Valid @RequestBody CreatePlayerRequest request
    ) {
        PlayerResponse response = playerService.createPlayer(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{playerId}")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    public ResponseEntity<PlayerResponse> getPlayer(
            @PathVariable String playerId
    ) {
        return ResponseEntity.ok(
                playerService.getPlayerById(playerId)
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    public ResponseEntity<PageResponse<PlayerResponse>> getPlayers(
            @RequestParam(required = false) PlayerStatus status,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return ResponseEntity.ok(
                playerService.getPlayers(status, page, size)
        );
    }

    @PatchMapping("/{playerId}")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    public ResponseEntity<PlayerResponse> updatePlayer(
            @PathVariable String playerId,
            @Valid @RequestBody UpdatePlayerRequest request
    ) {
        return ResponseEntity.ok(
                playerService.updatePlayer(playerId, request)
        );
    }

}
