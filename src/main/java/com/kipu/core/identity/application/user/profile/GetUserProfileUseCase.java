package com.kipu.core.identity.application.user.profile;

import com.kipu.core.identity.domain.exception.UserNotFoundException;
import com.kipu.core.identity.domain.model.User;
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

  public UserProfileResult execute(UUID userId) {
    log.info("[GetUserProfileUseCase] Starting get profile for user id: {}", userId);

    Optional<User> userOpt = userRepository.findById(userId);
    if (userOpt.isEmpty()) {
      log.warn("[GetUserProfileUseCase] User not found for id: {}", userId);
      throw new UserNotFoundException();
    }
    User user = userOpt.get();

    log.info("[GetUserProfileUseCase] Profile retrieved successfully for user id: {}", userId);
    return new UserProfileResult(
        user.getId(), user.getEmail(), user.isActive(), user.getCreatedAt());
  }
}
