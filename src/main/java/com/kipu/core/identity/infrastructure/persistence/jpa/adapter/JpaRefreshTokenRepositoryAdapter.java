package com.kipu.core.identity.infrastructure.persistence.jpa.adapter;

import com.kipu.core.identity.domain.repository.RefreshTokenRepository;
import com.kipu.core.identity.infrastructure.persistence.jpa.entity.RefreshTokenJpaEntity;
import com.kipu.core.identity.infrastructure.persistence.jpa.repository.JpaRefreshTokenRepository;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaRefreshTokenRepositoryAdapter implements RefreshTokenRepository {

  private final JpaRefreshTokenRepository jpaRefreshTokenRepository;

  @Override
  public void save(UUID userId, String token, OffsetDateTime expiresAt) {
    jpaRefreshTokenRepository.save(
        new RefreshTokenJpaEntity(UUID.randomUUID(), userId, token, expiresAt, false));
  }

  @Override
  public void deleteByUserId(UUID userId) {
    jpaRefreshTokenRepository.deleteByUserId(userId);
  }

  @Override
  public void revokeByToken(String token) {
    jpaRefreshTokenRepository.revokeByToken(token);
  }

  @Override
  public boolean existsValidToken(String token) {
    return jpaRefreshTokenRepository.existsByTokenAndRevokedFalseAndExpiryDateAfter(
        token, OffsetDateTime.now());
  }
}
