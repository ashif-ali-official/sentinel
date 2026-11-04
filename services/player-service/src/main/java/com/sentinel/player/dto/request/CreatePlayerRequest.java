package com.sentinel.player.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreatePlayerRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50)
        @Pattern(
                regexp = "^[a-zA-Z0-9_]+$",
                message = "Username can contain only letters, numbers and underscores"
        )
        String username,

        @NotBlank(message = "Display name is required")
        @Size(max = 100)
        String displayName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email address")
        @Size(max = 255)
        String email,

        @NotBlank(message = "Region is required")
        @Size(max = 50)
        String region
) {
}
