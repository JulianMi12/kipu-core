package com.kipu.core.identity.infrastructure.persistence.jpa.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.identity.infrastructure.persistence.jpa.entity.RefreshTokenJpaEntity;
import com.kipu.core.identity.infrastructure.persistence.jpa.repository.JpaRefreshTokenRepository;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JpaRefreshTokenRepositoryAdapterTest {

  @Mock private JpaRefreshTokenRepository jpaRefreshTokenRepository;

  @InjectMocks private JpaRefreshTokenRepositoryAdapter adapter;

  @Test
  void save_ShouldMapToEntityAndSave() {
    // Arrange
    UUID userId = UUID.randomUUID();
    String token = "some-refresh-token";
    OffsetDateTime expiresAt = OffsetDateTime.now().plusDays(1);

    ArgumentCaptor<RefreshTokenJpaEntity> entityCaptor =
        ArgumentCaptor.forClass(RefreshTokenJpaEntity.class);

    // Act
    adapter.save(userId, token, expiresAt);

    // Assert
    verify(jpaRefreshTokenRepository).save(entityCaptor.capture());
    RefreshTokenJpaEntity savedEntity = entityCaptor.getValue();

    assertThat(savedEntity.getUserId()).isEqualTo(userId);
    assertThat(savedEntity.getToken()).isEqualTo(token);
    assertThat(savedEntity.getExpiryDate()).isEqualTo(expiresAt);
    assertThat(savedEntity.isRevoked()).isFalse();
    assertThat(savedEntity.getId()).isNotNull();
  }

  @Test
  void deleteByUserId_ShouldDelegateToRepository() {
    // Arrange
    UUID userId = UUID.randomUUID();

    // Act
    adapter.deleteByUserId(userId);

    // Assert
    verify(jpaRefreshTokenRepository).deleteByUserId(userId);
  }

  @Test
  void revokeByToken_ShouldDelegateToRepository() {
    // Arrange
    String token = "token-to-revoke";

    // Act
    adapter.revokeByToken(token);

    // Assert
    verify(jpaRefreshTokenRepository).revokeByToken(token);
  }

  @Test
  void existsValidToken_ShouldReturnRepositoryResult() {
    // Arrange
    String token = "valid-token";
    when(jpaRefreshTokenRepository.existsByTokenAndRevokedFalseAndExpiryDateAfter(any(), any()))
        .thenReturn(true);

    // Act
    boolean result = adapter.existsValidToken(token);

    // Assert
    assertThat(result).isTrue();
    verify(jpaRefreshTokenRepository)
        .existsByTokenAndRevokedFalseAndExpiryDateAfter(
            any(String.class), any(OffsetDateTime.class));
  }
}
