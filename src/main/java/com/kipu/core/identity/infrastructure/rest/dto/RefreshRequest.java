package com.kipu.core.identity.infrastructure.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
    @NotBlank(message = "The refresh token is required.") String refreshToken) {}
