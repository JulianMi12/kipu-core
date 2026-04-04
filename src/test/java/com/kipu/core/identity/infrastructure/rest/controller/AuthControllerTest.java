package com.kipu.core.identity.infrastructure.rest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.identity.application.auth.login.LoginCommand;
import com.kipu.core.identity.application.auth.login.LoginResult;
import com.kipu.core.identity.application.auth.login.LoginUserUseCase;
import com.kipu.core.identity.application.auth.logout.LogoutUseCase;
import com.kipu.core.identity.application.auth.refresh.RefreshCommand;
import com.kipu.core.identity.application.auth.refresh.RefreshUseCase;
import com.kipu.core.identity.application.registration.RegisterUserCommand;
import com.kipu.core.identity.application.registration.RegisterUserUseCase;
import com.kipu.core.identity.application.registration.RegistrationResult;
import com.kipu.core.identity.domain.model.AuthTokens;
import com.kipu.core.identity.infrastructure.rest.dto.LoginRequest;
import com.kipu.core.identity.infrastructure.rest.dto.LoginResponse;
import com.kipu.core.identity.infrastructure.rest.dto.RefreshRequest;
import com.kipu.core.identity.infrastructure.rest.dto.RefreshResponse;
import com.kipu.core.identity.infrastructure.rest.dto.RegisterUserRequest;
import com.kipu.core.identity.infrastructure.rest.dto.RegistrationResponse;
import com.kipu.core.identity.infrastructure.rest.mapper.UserRestMapper;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

  @Mock private RegisterUserUseCase registerUserUseCase;
  @Mock private LoginUserUseCase loginUserUseCase;
  @Mock private RefreshUseCase refreshUseCase;
  @Mock private LogoutUseCase logoutUseCase;
  @Mock private UserRestMapper userRestMapper;

  @InjectMocks private AuthController authController;

  @Test
  void registerUser_ReturnsCreatedAndResponse_WhenRequestIsValid() {
    // Arrange
    RegisterUserRequest request = new RegisterUserRequest("test@kipu.com", "Password123!");
    RegisterUserCommand command = new RegisterUserCommand("test@kipu.com", "Password123!");
    AuthTokens dummyTokens = new AuthTokens("dummy-access", "dummy-refresh", OffsetDateTime.now());
    RegistrationResult result =
        new RegistrationResult(UUID.randomUUID(), "test@kipu.com", dummyTokens);
    RegistrationResponse expectedResponse =
        RegistrationResponse.builder().userId(result.userId()).email(result.email()).build();

    when(userRestMapper.toCommand(request)).thenReturn(command);
    when(registerUserUseCase.execute(command)).thenReturn(result);
    when(userRestMapper.toRegistrationResponse(result)).thenReturn(expectedResponse);

    // Act
    ResponseEntity<RegistrationResponse> response = authController.registerUser(request);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isEqualTo(expectedResponse);
    verify(registerUserUseCase).execute(command);
  }

  @Test
  void login_ReturnsOkAndTokens_WhenCredentialsAreValid() {
    // Arrange
    LoginRequest request = new LoginRequest("test@kipu.com", "Password123!");
    LoginCommand command = new LoginCommand("test@kipu.com", "Password123!");
    AuthTokens dummyTokens = new AuthTokens("dummy-access", "dummy-refresh", OffsetDateTime.now());
    LoginResult result = new LoginResult(UUID.randomUUID(), "test@kipu.com", dummyTokens);
    LoginResponse expectedResponse =
        LoginResponse.builder().userId(UUID.randomUUID()).email("test@kipu.com").build();

    when(userRestMapper.toCommand(request)).thenReturn(command);
    when(loginUserUseCase.execute(command)).thenReturn(result);
    when(userRestMapper.toLoginResponse(result)).thenReturn(expectedResponse);

    // Act
    ResponseEntity<LoginResponse> response = authController.login(request);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(expectedResponse);
    verify(loginUserUseCase).execute(command);
  }

  @Test
  void refresh_ReturnsOkAndNewTokens_WhenRefreshTokenIsValid() {
    // Arrange
    RefreshRequest request = new RefreshRequest("old-refresh-token");
    RefreshCommand command = new RefreshCommand("old-refresh-token");
    AuthTokens tokens = new AuthTokens("new-access", "new-refresh", OffsetDateTime.now());
    RefreshResponse expectedResponse =
        RefreshResponse.builder().accessToken("new-access").refreshToken("new-refresh").build();

    when(userRestMapper.toCommand(request)).thenReturn(command);
    when(refreshUseCase.execute(command)).thenReturn(tokens);
    when(userRestMapper.toRefreshResponse(tokens)).thenReturn(expectedResponse);

    // Act
    ResponseEntity<RefreshResponse> response = authController.refresh(request);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(expectedResponse);
    verify(refreshUseCase).execute(command);
  }

  @Test
  void logout_ReturnsNoContent_WhenCalledWithValidUserId() {
    // Arrange
    UUID userId = UUID.randomUUID();

    // Act
    ResponseEntity<Void> response = authController.logout(userId);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(response.getBody()).isNull();
    verify(logoutUseCase).execute(userId);
  }
}
