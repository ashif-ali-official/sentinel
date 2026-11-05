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
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
@RestController
@RequestMapping("/api/v1/players")
@RequiredArgsConstructor
@Validated
public class PlayerController {

    private final PlayerService playerService;

    @PostMapping
    public ResponseEntity<PlayerResponse> createPlayer(
            @Valid @RequestBody CreatePlayerRequest request
    ) {
        PlayerResponse response = playerService.createPlayer(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{playerId}")
    public ResponseEntity<PlayerResponse> getPlayer(
            @PathVariable String playerId
    ) {
        return ResponseEntity.ok(
                playerService.getPlayerById(playerId)
        );
    }

    @GetMapping
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
    public ResponseEntity<PlayerResponse> updatePlayer(
            @PathVariable String playerId,
            @Valid @RequestBody UpdatePlayerRequest request
    ) {
        return ResponseEntity.ok(
                playerService.updatePlayer(playerId, request)
        );
    }

}
