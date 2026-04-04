package com.kipu.core.identity.application.registration;

import com.kipu.core.identity.application.port.out.PasswordEncoderPort;
import com.kipu.core.identity.application.port.out.TokenProviderPort;
import com.kipu.core.identity.domain.exception.UserAlreadyExistsException;
import com.kipu.core.identity.domain.model.AuthTokens;
import com.kipu.core.identity.domain.model.User;
import com.kipu.core.identity.domain.model.UserKyc;
import com.kipu.core.identity.domain.repository.RefreshTokenRepository;
import com.kipu.core.identity.domain.repository.UserKycRepository;
import com.kipu.core.identity.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RegisterUserUseCase {

  private final UserRepository userRepository;
  private final TokenProviderPort tokenProviderPort;
  private final UserKycRepository userKycRepository;
  private final PasswordEncoderPort passwordEncoderPort;
  private final RefreshTokenRepository refreshTokenRepository;

  public RegistrationResult execute(RegisterUserCommand command) {
    log.info("[RegisterUserUseCase] Starting registration process");

    if (userRepository.findByEmail(command.email()).isPresent()) {
      log.warn("[RegisterUserUseCase] Registration failed - email already in use");
      throw new UserAlreadyExistsException(command.email());
    }

    String encodedPassword = passwordEncoderPort.encode(command.password());
    User user = User.create(command.email(), encodedPassword);
    userRepository.save(user);
    userKycRepository.save(UserKyc.createPending(user.getId()));

    AuthTokens tokens = tokenProviderPort.generate(user);
    refreshTokenRepository.save(
        user.getId(), tokens.refreshToken(), tokens.refreshTokenExpiresAt());

    log.info(
        "[RegisterUserUseCase] Registration completed successfully for user id: {}", user.getId());
    return new RegistrationResult(user.getId(), user.getEmail(), tokens);
  }
}
