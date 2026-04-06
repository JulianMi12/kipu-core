package com.kipu.core.identity.application.user.profile;

import com.kipu.core.identity.domain.exception.UserNotFoundException;
import com.kipu.core.identity.domain.model.KycStatus;
import com.kipu.core.identity.domain.model.User;
import com.kipu.core.identity.domain.model.UserKyc;
import com.kipu.core.identity.domain.repository.UserKycRepository;
import com.kipu.core.identity.domain.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetUserProfileUseCase {

  private final UserRepository userRepository;
  private final UserKycRepository userKycRepository;

  public UserProfileResult execute(UUID userId) {
    log.info("[GetUserProfileUseCase] Starting get profile for user id: {}", userId);

    Optional<User> userOpt = userRepository.findById(userId);
    if (userOpt.isEmpty()) {
      log.error("[GetUserProfileUseCase] User not found for id: {}", userId);
      throw new UserNotFoundException();
    }
    User user = userOpt.get();

    Optional<UserKyc> kycOpt = userKycRepository.findByUserId(userId);
    KycStatus kycStatus = kycOpt.map(UserKyc::getStatus).orElse(KycStatus.PENDING);
    boolean onboardingCompleted = kycOpt.map(UserKyc::isOnboardingCompleted).orElse(false);

    log.info("[GetUserProfileUseCase] Profile retrieved successfully for user id: {}", userId);
    return new UserProfileResult(
        user.getId(),
        user.getEmail(),
        user.isActive(),
        user.getCreatedAt(),
        kycStatus,
        onboardingCompleted);
  }
}
