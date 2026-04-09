package com.kipu.core.identity.application.user.onboarding;

import com.kipu.core.identity.application.user.profile.UserProfileResult;
import com.kipu.core.identity.domain.exception.ProfileSyncException;
import com.kipu.core.identity.domain.exception.UserNotFoundException;
import com.kipu.core.identity.domain.model.User;
import com.kipu.core.identity.domain.model.UserKyc;
import com.kipu.core.identity.domain.port.out.ContactProfileInfo;
import com.kipu.core.identity.domain.port.out.ProfileSyncPort;
import com.kipu.core.identity.domain.repository.UserKycRepository;
import com.kipu.core.identity.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompleteOnboardingUseCase {

  private final UserRepository userRepository;
  private final ProfileSyncPort profileSyncPort;
  private final UserKycRepository userKycRepository;

  @Transactional
  public UserProfileResult execute(CompleteOnboardingCommand command) {
    log.info("[CompleteOnboardingUseCase] Starting onboarding for user id: {}", command.userId());

    User user = this.getUser(command);
    UserKyc userKyc = this.getUserKyc(command);

    ContactProfileInfo contactInfo = this.syncWithContactsModule(user.getEmail(), command);

    userKyc.completeOnboarding(contactInfo.contactId());
    userKycRepository.save(userKyc);

    log.info(
        "[CompleteOnboardingUseCase] Onboarding completed successfully for user id: {}",
        command.userId());
    return new UserProfileResult(
        user.getId(),
        user.getEmail(),
        user.isActive(),
        user.getCreatedAt(),
        userKyc.getStatus(),
        userKyc.isOnboardingCompleted(),
        contactInfo.firstName(),
        contactInfo.lastName(),
        contactInfo.contactId());
  }

  private UserKyc getUserKyc(CompleteOnboardingCommand command) {
    return userKycRepository
        .findByUserId(command.userId())
        .orElseThrow(
            () -> {
              log.error(
                  "[CompleteOnboardingUseCase] KYC record not found for user id: {}",
                  command.userId());
              return new UserNotFoundException();
            });
  }

  private User getUser(CompleteOnboardingCommand command) {
    return userRepository
        .findById(command.userId())
        .orElseThrow(
            () -> {
              log.error("[CompleteOnboardingUseCase] User not found for id: {}", command.userId());
              return new UserNotFoundException();
            });
  }

  private ContactProfileInfo syncWithContactsModule(
      String email, CompleteOnboardingCommand command) {
    try {
      return profileSyncPort.createSelfContact(
          command.userId(),
          command.firstName(),
          command.lastName(),
          email,
          command.birthdate(),
          command.timezone());
    } catch (Exception e) {
      log.error(
          "[CompleteOnboardingUseCase] Failed to sync profile with Contacts module for user: {}",
          command.userId(),
          e);
      throw new ProfileSyncException("Could not create self-contact profile", e);
    }
  }
}
