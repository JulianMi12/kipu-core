package com.kipu.core.identity.domain.model;

import java.time.OffsetDateTime;

public record AuthTokens(
    String accessToken, String refreshToken, OffsetDateTime refreshTokenExpiresAt) {}
