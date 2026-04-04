package com.kipu.core.identity.application.auth.login;

import com.kipu.core.identity.application.port.out.PasswordEncoderPort;
import com.kipu.core.identity.application.port.out.TokenProviderPort;
import com.kipu.core.identity.domain.exception.InvalidCredentialsException;
import com.kipu.core.identity.domain.model.AuthTokens;
import com.kipu.core.identity.domain.model.User;
import com.kipu.core.identity.domain.repository.RefreshTokenRepository;
import com.kipu.core.identity.domain.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LoginUserUseCase {

  private final UserRepository userRepository;
  private final PasswordEncoderPort passwordEncoderPort;
  private final TokenProviderPort tokenProviderPort;
  private final RefreshTokenRepository refreshTokenRepository;

  public LoginResult execute(LoginCommand command) {
    log.info("[LoginUserUseCase] Starting login process");

    Optional<User> userOpt = userRepository.findByEmail(command.email());
    if (userOpt.isEmpty()) {
      log.warn("[LoginUserUseCase] Login failed - user not found");
      throw new InvalidCredentialsException();
    }
    User user = userOpt.get();

    if (!passwordEncoderPort.matches(command.password(), user.getPasswordHash())) {
      log.warn(
          "[LoginUserUseCase] Login failed - invalid credentials for user id: {}", user.getId());
      throw new InvalidCredentialsException();
    }

    refreshTokenRepository.deleteByUserId(user.getId());

    AuthTokens tokens = tokenProviderPort.generate(user);
    refreshTokenRepository.save(
        user.getId(), tokens.refreshToken(), tokens.refreshTokenExpiresAt());

    log.info("[LoginUserUseCase] Login completed successfully for user id: {}", user.getId());
    return new LoginResult(user.getId(), user.getEmail(), tokens);
  }
}
