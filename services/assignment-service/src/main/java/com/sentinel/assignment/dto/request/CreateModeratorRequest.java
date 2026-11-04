package com.sentinel.assignment.dto.request;

import jakarta.validation.constraints.*;

public record CreateModeratorRequest(

        @NotBlank
        @Size(max = 50)
        String username,

        @NotBlank
        @Size(max = 100)
        String displayName,

        @NotBlank
        @Size(max = 50)
        String region,

        @NotNull
        @Min(1)
        @Max(100)
        Integer maxActiveCases

) {}