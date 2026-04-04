package com.kipu.core.identity.application.auth.refresh;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.identity.application.port.out.TokenProviderPort;
import com.kipu.core.identity.domain.exception.InvalidTokenException;
import com.kipu.core.identity.domain.model.AuthTokens;
import com.kipu.core.identity.domain.model.User;
import com.kipu.core.identity.domain.repository.RefreshTokenRepository;
import com.kipu.core.identity.domain.repository.UserRepository;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RefreshUseCaseTest {

  @Mock private TokenProviderPort tokenProviderPort;
  @Mock private UserRepository userRepository;
  @Mock private RefreshTokenRepository refreshTokenRepository;

  @InjectMocks private RefreshUseCase refreshUseCase;

  @Test
  void execute_ShouldReturnNewTokens_WhenRefreshTokenIsValid() {
    // Arrange
    String oldRefreshToken = "valid-old-refresh-token";
    RefreshCommand command = new RefreshCommand(oldRefreshToken);
    UUID userId = UUID.randomUUID();
    User user = User.reconstitute(userId, "test@kipu.com", "hash", true, OffsetDateTime.now());
    AuthTokens newTokens =
        new AuthTokens("new-access", "new-refresh", OffsetDateTime.now().plusDays(1));

    when(tokenProviderPort.isRefreshTokenValid(oldRefreshToken)).thenReturn(true);
    when(tokenProviderPort.extractUserId(oldRefreshToken)).thenReturn(userId);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(tokenProviderPort.generate(user)).thenReturn(newTokens);

    // Act
    AuthTokens result = refreshUseCase.execute(command);

    // Assert
    assertThat(result).isEqualTo(newTokens);
    verify(refreshTokenRepository).deleteByUserId(userId);
    verify(refreshTokenRepository)
        .save(userId, newTokens.refreshToken(), newTokens.refreshTokenExpiresAt());
  }

  @Test
  void execute_ShouldThrowInvalidTokenException_WhenTokenIsInvalid() {
    // Arrange
    String invalidToken = "invalid-token";
    RefreshCommand command = new RefreshCommand(invalidToken);
    when(tokenProviderPort.isRefreshTokenValid(invalidToken)).thenReturn(false);

    // Act & Assert
    assertThatThrownBy(() -> refreshUseCase.execute(command))
        .isInstanceOf(InvalidTokenException.class)
        .hasMessage("The provided refresh token is invalid or has expired.");

    verify(userRepository, never()).findById(any());
    verify(refreshTokenRepository, never()).deleteByUserId(any());
  }

  @Test
  void execute_ShouldThrowInvalidTokenException_WhenUserDoesNotExist() {
    // Arrange
    String validToken = "valid-token-but-no-user";
    RefreshCommand command = new RefreshCommand(validToken);
    UUID userId = UUID.randomUUID();

    when(tokenProviderPort.isRefreshTokenValid(validToken)).thenReturn(true);
    when(tokenProviderPort.extractUserId(validToken)).thenReturn(userId);
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> refreshUseCase.execute(command))
        .isInstanceOf(InvalidTokenException.class)
        .hasMessage("User associated with token not found.");

    verify(refreshTokenRepository, never()).deleteByUserId(any());
  }
}
