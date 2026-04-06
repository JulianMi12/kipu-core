package com.kipu.core.identity.application.auth.refresh;

import com.kipu.core.identity.application.port.out.TokenProviderPort;
import com.kipu.core.identity.domain.exception.InvalidTokenException;
import com.kipu.core.identity.domain.model.AuthTokens;
import com.kipu.core.identity.domain.model.User;
import com.kipu.core.identity.domain.repository.RefreshTokenRepository;
import com.kipu.core.identity.domain.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RefreshUseCase {

  private final UserRepository userRepository;
  private final TokenProviderPort tokenProviderPort;
  private final RefreshTokenRepository refreshTokenRepository;

  public AuthTokens execute(RefreshCommand command) {
    log.info("[RefreshUseCase] Starting token refresh process");

    if (!tokenProviderPort.isRefreshTokenValid(command.refreshToken())) {
      log.warn("[RefreshUseCase] Token refresh failed - invalid or expired refresh token");
      throw new InvalidTokenException("The provided refresh token is invalid or has expired.");
    }

    UUID userId = tokenProviderPort.extractUserId(command.refreshToken());

    Optional<User> userOpt = userRepository.findById(userId);
    if (userOpt.isEmpty()) {
      log.error("[RefreshUseCase] Token refresh failed - user not found for id: {}", userId);
      throw new InvalidTokenException("User associated with token not found.");
    }
    User user = userOpt.get();

    refreshTokenRepository.deleteByUserId(user.getId());

    AuthTokens tokens = tokenProviderPort.generate(user);
    refreshTokenRepository.save(
        user.getId(), tokens.refreshToken(), tokens.refreshTokenExpiresAt());

    log.info("[RefreshUseCase] Token refresh completed successfully for user id: {}", user.getId());
    return tokens;
  }
}
