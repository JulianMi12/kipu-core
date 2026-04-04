package com.kipu.core.identity.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.kipu.core.identity.domain.model.AuthTokens;
import com.kipu.core.identity.domain.model.User;
import com.kipu.core.identity.domain.repository.RefreshTokenRepository;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderAdapterTest {

  @Mock private RefreshTokenRepository refreshTokenRepository;

  private JwtTokenProviderAdapter jwtTokenProviderAdapter;

  private final String secret =
      Base64.getEncoder()
          .encodeToString(
              "esta-es-una-clave-secreta-de-prueba-muy-larga-para-kipu-2026".getBytes());
  private final long accessExpiration = 3600000;
  private final long refreshExpiration = 86400000;

  @BeforeEach
  void setUp() {
    jwtTokenProviderAdapter =
        new JwtTokenProviderAdapter(
            secret, accessExpiration, refreshExpiration, refreshTokenRepository);
  }

  @Test
  void generate_ShouldReturnValidTokens() {
    // Arrange
    User user =
        User.reconstitute(UUID.randomUUID(), "test@kipu.com", "hash", true, OffsetDateTime.now());

    // Act
    AuthTokens tokens = jwtTokenProviderAdapter.generate(user);

    // Assert
    assertThat(tokens).isNotNull();
    assertThat(tokens.accessToken()).isNotEmpty();
    assertThat(tokens.refreshToken()).isNotEmpty();
    assertThat(tokens.refreshTokenExpiresAt()).isAfter(OffsetDateTime.now());

    UUID extractedId = jwtTokenProviderAdapter.extractUserId(tokens.accessToken());
    assertThat(extractedId).isEqualTo(user.getId());
  }

  @Test
  void isAccessTokenValid_ShouldReturnTrue_WhenTokenIsValid() {
    // Arrange
    User user =
        User.reconstitute(UUID.randomUUID(), "test@kipu.com", "hash", true, OffsetDateTime.now());
    String token = jwtTokenProviderAdapter.generate(user).accessToken();

    // Act
    boolean isValid = jwtTokenProviderAdapter.isAccessTokenValid(token);

    // Assert
    assertThat(isValid).isTrue();
  }

  @Test
  void isAccessTokenValid_ShouldReturnFalse_WhenTokenIsMalformed() {
    // Act
    boolean isValid = jwtTokenProviderAdapter.isAccessTokenValid("not.a.valid.token");

    // Assert
    assertThat(isValid).isFalse();
  }

  @Test
  void isRefreshTokenValid_ShouldReturnTrue_WhenTokenIsValidAndExistsInRepo() {
    // Arrange
    User user =
        User.reconstitute(UUID.randomUUID(), "test@kipu.com", "hash", true, OffsetDateTime.now());
    String token = jwtTokenProviderAdapter.generate(user).refreshToken();

    when(refreshTokenRepository.existsValidToken(token)).thenReturn(true);

    // Act
    boolean isValid = jwtTokenProviderAdapter.isRefreshTokenValid(token);

    // Assert
    assertThat(isValid).isTrue();
    verify(refreshTokenRepository).existsValidToken(token);
  }

  @Test
  void isRefreshTokenValid_ShouldReturnFalse_WhenTokenIsExpiredOrRevokedInRepo() {
    // Arrange
    User user =
        User.reconstitute(UUID.randomUUID(), "test@kipu.com", "hash", true, OffsetDateTime.now());
    String token = jwtTokenProviderAdapter.generate(user).refreshToken();

    when(refreshTokenRepository.existsValidToken(token)).thenReturn(false);

    // Act
    boolean isValid = jwtTokenProviderAdapter.isRefreshTokenValid(token);

    // Assert
    assertThat(isValid).isFalse();
  }

  @Test
  void extractUserId_ShouldReturnCorrectId() {
    // Arrange
    UUID userId = UUID.randomUUID();
    User user = User.reconstitute(userId, "test@kipu.com", "hash", true, OffsetDateTime.now());
    String token = jwtTokenProviderAdapter.generate(user).accessToken();

    // Act
    UUID extractedId = jwtTokenProviderAdapter.extractUserId(token);

    // Assert
    assertThat(extractedId).isEqualTo(userId);
  }

  @Test
  void isRefreshTokenValid_ShouldReturnFalse_WhenTokenIsMalformedOrInvalid() {
    // Arrange
    String malformedToken = "this.is.not.a.valid.jwt.token";

    // Act
    boolean result = jwtTokenProviderAdapter.isRefreshTokenValid(malformedToken);

    // Assert
    assertThat(result).isFalse();
    verifyNoInteractions(refreshTokenRepository);
  }
}
