package com.kipu.core.identity.domain.repository;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface RefreshTokenRepository {

  void save(UUID userId, String token, OffsetDateTime expiresAt);

  void deleteByUserId(UUID userId);

  void revokeByToken(String token);

  boolean existsValidToken(String token);
}
