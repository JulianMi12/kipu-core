package com.kipu.core.identity.application.auth.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.identity.application.port.out.PasswordEncoderPort;
import com.kipu.core.identity.application.port.out.TokenProviderPort;
import com.kipu.core.identity.domain.exception.InvalidCredentialsException;
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
class LoginUserUseCaseTest {

  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoderPort passwordEncoderPort;
  @Mock private TokenProviderPort tokenProviderPort;
  @Mock private RefreshTokenRepository refreshTokenRepository;

  @InjectMocks private LoginUserUseCase loginUserUseCase;

  @Test
  void execute_ShouldReturnLoginResult_WhenCredentialsAreValid() {
    // Arrange
    String email = "test@kipu.com";
    String password = "Password123!";
    LoginCommand command = new LoginCommand(email, password);

    User user =
        User.reconstitute(UUID.randomUUID(), email, "hashed-password", true, OffsetDateTime.now());
    AuthTokens tokens = new AuthTokens("access", "refresh", OffsetDateTime.now().plusDays(1));

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(passwordEncoderPort.matches(password, user.getPasswordHash())).thenReturn(true);
    when(tokenProviderPort.generate(user)).thenReturn(tokens);

    // Act
    LoginResult result = loginUserUseCase.execute(command);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.userId()).isEqualTo(user.getId());
    assertThat(result.tokens()).isEqualTo(tokens);

    verify(refreshTokenRepository).deleteByUserId(user.getId());
    verify(refreshTokenRepository)
        .save(user.getId(), tokens.refreshToken(), tokens.refreshTokenExpiresAt());
  }

  @Test
  void execute_ShouldThrowInvalidCredentials_WhenUserNotFound() {
    // Arrange
    LoginCommand command = new LoginCommand("nonexistent@kipu.com", "any");
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> loginUserUseCase.execute(command))
        .isInstanceOf(InvalidCredentialsException.class);

    verify(passwordEncoderPort, never()).matches(anyString(), anyString());
    verify(tokenProviderPort, never()).generate(any());
  }

  @Test
  void execute_ShouldThrowInvalidCredentials_WhenPasswordDoesNotMatch() {
    // Arrange
    String email = "test@kipu.com";
    LoginCommand command = new LoginCommand(email, "wrong-password");
    User user =
        User.reconstitute(UUID.randomUUID(), email, "hashed-password", true, OffsetDateTime.now());

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(passwordEncoderPort.matches(anyString(), anyString())).thenReturn(false);

    // Act & Assert
    assertThatThrownBy(() -> loginUserUseCase.execute(command))
        .isInstanceOf(InvalidCredentialsException.class);

    verify(refreshTokenRepository, never()).deleteByUserId(any());
    verify(tokenProviderPort, never()).generate(any());
  }
}
