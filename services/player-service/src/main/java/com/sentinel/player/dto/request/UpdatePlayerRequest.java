package com.sentinel.player.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdatePlayerRequest(

        @Size(max = 100)
        String displayName,

        @Email(message = "Invalid email address")
        @Size(max = 255)
        String email,

        @Size(max = 50)
        String region
) {}
