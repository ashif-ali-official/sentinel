package com.sentinel.player.service;

import com.sentinel.player.dto.request.CreatePlayerRequest;
import com.sentinel.player.dto.request.UpdatePlayerRequest;
import com.sentinel.player.entity.Player;
import com.sentinel.player.entity.enums.PlayerStatus;
import com.sentinel.player.exception.DuplicatePlayerException;
import com.sentinel.player.exception.InvalidPlayerRequestException;
import com.sentinel.player.exception.PlayerNotFoundException;
import com.sentinel.player.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock private PlayerRepository playerRepository;
    @InjectMocks private PlayerService playerService;

    @Test
    void createsAndMapsPlayer() {
        CreatePlayerRequest request = new CreatePlayerRequest("player_1", "Player One", "p1@example.com", "APAC");
        when(playerRepository.save(any(Player.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = playerService.createPlayer(request);

        assertNotNull(response.playerId());
        assertEquals("player_1", response.username());
        assertEquals("Player One", response.displayName());
        assertEquals("p1@example.com", response.email());
        assertEquals("APAC", response.region());
        assertEquals(PlayerStatus.ACTIVE, response.status());
        verify(playerRepository).existsByUsername("player_1");
        verify(playerRepository).existsByEmail("p1@example.com");
        verify(playerRepository).save(any(Player.class));
    }

    @Test
    void rejectsDuplicateUsernameBeforeCheckingEmail() {
        when(playerRepository.existsByUsername("player_1")).thenReturn(true);

        assertThrows(DuplicatePlayerException.class, () -> playerService.createPlayer(
                new CreatePlayerRequest("player_1", "Player One", "p1@example.com", "APAC")));

        verify(playerRepository, never()).existsByEmail(any());
        verify(playerRepository, never()).save(any());
    }

    @Test
    void rejectsDuplicateEmail() {
        when(playerRepository.existsByEmail("p1@example.com")).thenReturn(true);

        assertThrows(DuplicatePlayerException.class, () -> playerService.createPlayer(
                new CreatePlayerRequest("player_1", "Player One", "p1@example.com", "APAC")));

        verify(playerRepository, never()).save(any());
    }

    @Test
    void findsPlayerByIdAndEmail() {
        Player player = player();
        when(playerRepository.findById("id-1")).thenReturn(Optional.of(player));
        when(playerRepository.findByEmailIgnoreCase("p1@example.com")).thenReturn(Optional.of(player));

        assertEquals("id-1", playerService.getPlayerById("id-1").playerId());
        assertEquals("p1@example.com", playerService.getPlayerByEmail("p1@example.com").email());
    }

    @Test
    void missingPlayerLookupsThrowNotFound() {
        when(playerRepository.findById("missing")).thenReturn(Optional.empty());
        when(playerRepository.findByEmailIgnoreCase("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(PlayerNotFoundException.class, () -> playerService.getPlayerById("missing"));
        assertThrows(PlayerNotFoundException.class, () -> playerService.getPlayerByEmail("missing@example.com"));
    }

    @Test
    void pagesAllPlayersAndStatusFilteredPlayers() {
        Player player = player();
        var page = new PageImpl<>(List.of(player), PageRequest.of(1, 2), 3);
        when(playerRepository.findAll(any(PageRequest.class))).thenReturn(page);
        when(playerRepository.findByStatus(eq(PlayerStatus.ACTIVE), any(PageRequest.class))).thenReturn(page);

        var all = playerService.getPlayers(null, 1, 2);
        var active = playerService.getPlayers(PlayerStatus.ACTIVE, 1, 2);

        assertEquals(1, all.content().size());
        assertEquals(1, all.page());
        assertEquals(3, all.totalElements());
        assertEquals(2, all.totalPages());
        assertTrue(all.last());
        assertEquals("id-1", active.content().getFirst().playerId());
        verify(playerRepository).findAll(any(PageRequest.class));
        verify(playerRepository).findByStatus(eq(PlayerStatus.ACTIVE), any(PageRequest.class));
    }

    @Test
    void rejectsEmptyUpdate() {
        assertThrows(InvalidPlayerRequestException.class,
                () -> playerService.updatePlayer("id-1", new UpdatePlayerRequest(null, null, null)));
        verifyNoInteractions(playerRepository);
    }

    @Test
    void updatesPlayerAndRejectsDuplicateEmail() {
        Player player = player();
        when(playerRepository.findById("id-1")).thenReturn(Optional.of(player));

        var updated = playerService.updatePlayer("id-1", new UpdatePlayerRequest("Updated", null, "EU"));
        assertEquals("Updated", updated.displayName());
        assertEquals("EU", updated.region());

        when(playerRepository.existsByEmailAndIdNot("other@example.com", "id-1")).thenReturn(true);
        assertThrows(DuplicatePlayerException.class, () -> playerService.updatePlayer(
                "id-1", new UpdatePlayerRequest(null, "other@example.com", null)));
    }

    @Test
    void updateEmailAndMissingPlayerCases() {
        Player player = player();
        when(playerRepository.findById("id-1")).thenReturn(Optional.of(player));
        when(playerRepository.existsByEmailAndIdNot("new@example.com", "id-1")).thenReturn(false);

        var updated = playerService.updatePlayer("id-1", new UpdatePlayerRequest(null, "new@example.com", null));
        assertEquals("new@example.com", updated.email());

        when(playerRepository.findById("missing")).thenReturn(Optional.empty());
        assertThrows(PlayerNotFoundException.class, () -> playerService.updatePlayer(
                "missing", new UpdatePlayerRequest("Updated", null, null)));
    }

    private Player player() {
        Player player = new Player("player_1", "Player One", "p1@example.com", "APAC");
        try {
            var id = Player.class.getDeclaredField("id");
            id.setAccessible(true);
            id.set(player, "id-1");
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(exception);
        }
        return player;
    }
}
