package com.kipu.core.identity.application.auth.logout;

import com.kipu.core.identity.domain.repository.RefreshTokenRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutUseCase {

  private final RefreshTokenRepository refreshTokenRepository;

  @Transactional
  public void execute(UUID userId) {
    log.info("[LogoutUseCase] Starting logout process for user id: {}", userId);
    refreshTokenRepository.deleteByUserId(userId);
    log.info("[LogoutUseCase] Logout completed successfully for user id: {}", userId);
  }
}
