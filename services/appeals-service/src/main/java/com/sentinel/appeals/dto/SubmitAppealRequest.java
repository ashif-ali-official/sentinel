package com.sentinel.appeals.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record SubmitAppealRequest(@NotBlank String playerId,@NotBlank @Size(max=2000) String reason){}
