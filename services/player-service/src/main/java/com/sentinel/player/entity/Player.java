package com.sentinel.player.entity;

import com.sentinel.player.entity.enums.PlayerStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "players")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Player {

    @Id
    private String id;

    @Column(nullable = false, length = 50, unique = true)
    private String username;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 50)
    private String region;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PlayerStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Player(
            String username,
            String displayName,
            String email,
            String region
    ) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.displayName = displayName;
        this.email = email;
        this.region = region;
        this.status = PlayerStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateProfile(String displayName, String email, String region) {
        if (displayName != null) {
            this.displayName = displayName;
        }

        if (email != null) {
            this.email = email;
        }

        if (region != null) {
            this.region = region;
        }
        this.updatedAt = LocalDateTime.now();
    }

}
