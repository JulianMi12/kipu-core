package com.kipu.core.identity.application.registration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.identity.application.port.out.PasswordEncoderPort;
import com.kipu.core.identity.application.port.out.TokenProviderPort;
import com.kipu.core.identity.domain.exception.UserAlreadyExistsException;
import com.kipu.core.identity.domain.model.AuthTokens;
import com.kipu.core.identity.domain.model.User;
import com.kipu.core.identity.domain.repository.RefreshTokenRepository;
import com.kipu.core.identity.domain.repository.UserKycRepository;
import com.kipu.core.identity.domain.repository.UserRepository;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoderPort passwordEncoderPort;
  @Mock private TokenProviderPort tokenProviderPort;
  @Mock private RefreshTokenRepository refreshTokenRepository;
  @Mock private UserKycRepository userKycRepository;

  @InjectMocks private RegisterUserUseCase registerUserUseCase;

  @Test
  void execute_ShouldRegisterUserAndReturnTokens_WhenEmailIsAvailable() {
    // Arrange
    String email = "newuser@kipu.com";
    String rawPassword = "Password123!";
    String encodedPassword = "encoded-hash";
    RegisterUserCommand command = new RegisterUserCommand(email, rawPassword);

    User mockUser =
        User.reconstitute(UUID.randomUUID(), email, encodedPassword, true, OffsetDateTime.now());
    AuthTokens tokens = new AuthTokens("access", "refresh", OffsetDateTime.now().plusDays(1));

    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
    when(passwordEncoderPort.encode(rawPassword)).thenReturn(encodedPassword);
    when(tokenProviderPort.generate(any(User.class))).thenReturn(tokens);

    try (MockedStatic<User> userMockedStatic = mockStatic(User.class)) {
      userMockedStatic.when(() -> User.create(email, encodedPassword)).thenReturn(mockUser);

      // Act
      RegistrationResult result = registerUserUseCase.execute(command);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result.userId()).isEqualTo(mockUser.getId());
      assertThat(result.tokens()).isEqualTo(tokens);

      verify(userRepository).save(mockUser);
      verify(refreshTokenRepository)
          .save(mockUser.getId(), tokens.refreshToken(), tokens.refreshTokenExpiresAt());

      verify(userKycRepository)
          .save(
              argThat(
                  kyc ->
                      kyc.getUserId().equals(mockUser.getId())
                          && kyc.getStatus().name().equals("PENDING")));
    }
  }

  @Test
  void execute_ShouldThrowUserAlreadyExistsException_WhenEmailInUse() {
    // Arrange
    String email = "existing@kipu.com";
    RegisterUserCommand command = new RegisterUserCommand(email, "password");
    User existingUser =
        User.reconstitute(UUID.randomUUID(), email, "hash", true, OffsetDateTime.now());

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

    // Act & Assert
    assertThatThrownBy(() -> registerUserUseCase.execute(command))
        .isInstanceOf(UserAlreadyExistsException.class);

    verify(passwordEncoderPort, never()).encode(anyString());
    verify(userRepository, never()).save(any());
  }
}
