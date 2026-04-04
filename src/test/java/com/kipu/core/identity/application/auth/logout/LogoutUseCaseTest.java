package com.kipu.core.identity.application.auth.logout;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.kipu.core.identity.domain.repository.RefreshTokenRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LogoutUseCaseTest {

  @Mock private RefreshTokenRepository refreshTokenRepository;

  @InjectMocks private LogoutUseCase logoutUseCase;

  @Test
  void execute_ShouldDeleteRefreshTokens_WhenUserIdIsProvided() {
    // Arrange
    UUID userId = UUID.randomUUID();

    // Act
    logoutUseCase.execute(userId);

    // Assert
    verify(refreshTokenRepository, times(1)).deleteByUserId(userId);
  }
}
