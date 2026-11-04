package com.sentinel.player.repository;

import com.sentinel.player.entity.Player;
import com.sentinel.player.entity.enums.PlayerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, String> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, String id);

    Optional<Player> findByUsername(String username);

    Page<Player> findByStatus(
            PlayerStatus status,
            Pageable pageable
    );

}
