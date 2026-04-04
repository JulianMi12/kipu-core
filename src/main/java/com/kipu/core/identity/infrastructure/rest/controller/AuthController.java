package com.kipu.core.identity.infrastructure.rest.controller;

import com.kipu.core.identity.application.auth.login.LoginResult;
import com.kipu.core.identity.application.auth.login.LoginUserUseCase;
import com.kipu.core.identity.application.auth.logout.LogoutUseCase;
import com.kipu.core.identity.application.auth.refresh.RefreshUseCase;
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
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

  private final RegisterUserUseCase registerUserUseCase;
  private final LoginUserUseCase loginUserUseCase;
  private final RefreshUseCase refreshUseCase;
  private final LogoutUseCase logoutUseCase;
  private final UserRestMapper userRestMapper;

  @Override
  @PostMapping("/register")
  public ResponseEntity<RegistrationResponse> registerUser(
      @Valid @RequestBody RegisterUserRequest request) {
    log.info("[AuthController] POST /register called");
    RegistrationResult result = registerUserUseCase.execute(userRestMapper.toCommand(request));
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(userRestMapper.toRegistrationResponse(result));
  }

  @Override
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
    log.info("[AuthController] POST /login called");
    LoginResult result = loginUserUseCase.execute(userRestMapper.toCommand(request));
    return ResponseEntity.ok(userRestMapper.toLoginResponse(result));
  }

  @Override
  @PostMapping("/refresh")
  public ResponseEntity<RefreshResponse> refresh(@Valid @RequestBody RefreshRequest request) {
    log.info("[AuthController] POST /refresh called");
    AuthTokens tokens = refreshUseCase.execute(userRestMapper.toCommand(request));
    return ResponseEntity.ok(userRestMapper.toRefreshResponse(tokens));
  }

  @Override
  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@AuthenticationPrincipal UUID userId) {
    log.info("[AuthController] POST /logout called for user id: {}", userId);
    logoutUseCase.execute(userId);
    return ResponseEntity.noContent().build();
  }
}
