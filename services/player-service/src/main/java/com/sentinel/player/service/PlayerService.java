package com.sentinel.player.service;

import com.sentinel.player.dto.request.CreatePlayerRequest;
import com.sentinel.player.dto.request.UpdatePlayerRequest;
import com.sentinel.player.dto.response.PageResponse;
import com.sentinel.player.dto.response.PlayerResponse;
import com.sentinel.player.entity.Player;
import com.sentinel.player.entity.enums.PlayerStatus;
import com.sentinel.player.exception.DuplicatePlayerException;
import com.sentinel.player.exception.InvalidPlayerRequestException;
import com.sentinel.player.exception.PlayerNotFoundException;
import com.sentinel.player.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlayerService {

    private final PlayerRepository playerRepository;

    @Transactional
    public PlayerResponse createPlayer(CreatePlayerRequest request) {
        if(playerRepository.existsByUsername(request.username())){
            throw new DuplicatePlayerException(
                    "Username already exists"
            );
        }

        if(playerRepository.existsByEmail(request.email())){
            throw new DuplicatePlayerException(
                    "Email already exists"
            );
        }

        Player player = new Player(
                request.username(),
                request.displayName(),
                request.email(),
                request.region()
        );

        Player savedPlayer = playerRepository.save(player);
        return mapToPlayerResponse(savedPlayer);
    }

    public PlayerResponse getPlayerById(String playerId) {
        Player player = findPlayer(playerId);
        return mapToPlayerResponse(player);
    }

    public PlayerResponse getPlayerByEmail(String email) {
        Player player = playerRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new PlayerNotFoundException("Player not found for signed-in email"));
        return mapToPlayerResponse(player);
    }

    public PageResponse<PlayerResponse> getPlayers(
            PlayerStatus status,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Player> players = status == null
                ? playerRepository.findAll(pageable)
                : playerRepository.findByStatus(status, pageable);

        return new PageResponse<>(
                players.getContent()
                        .stream()
                        .map(this::mapToPlayerResponse)
                        .toList(),
                players.getNumber(),
                players.getSize(),
                players.getTotalElements(),
                players.getTotalPages(),
                players.isLast()
        );
    }

    @Transactional
    public PlayerResponse updatePlayer(String playerId, UpdatePlayerRequest request) {

        if (request.displayName() == null
                && request.email() == null
                && request.region() == null) {
            throw new InvalidPlayerRequestException(
                    "At least one field must be provided for update"
            );
        }

        Player player = findPlayer(playerId);
        if(request.email() != null && playerRepository.existsByEmailAndIdNot(request.email(), playerId)){
            throw new DuplicatePlayerException(
                    "Email already exists"
            );
        }

        player.updateProfile(request.displayName(), request.email(), request.region());
        return mapToPlayerResponse(player);

    }

    private Player findPlayer(String playerId) {
        return playerRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(
                        "Player not found: " + playerId
                ));
    }

    private PlayerResponse mapToPlayerResponse(Player player){
        return new PlayerResponse(
                player.getId(),
                player.getUsername(),
                player.getDisplayName(),
                player.getEmail(),
                player.getRegion(),
                player.getStatus(),
                player.getCreatedAt(),
                player.getUpdatedAt()
        );
    }

}
