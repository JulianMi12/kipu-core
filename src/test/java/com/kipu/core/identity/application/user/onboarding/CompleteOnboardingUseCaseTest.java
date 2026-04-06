package com.kipu.core.identity.application.user.onboarding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.identity.application.user.profile.UserProfileResult;
import com.kipu.core.identity.domain.exception.ProfileSyncException;
import com.kipu.core.identity.domain.exception.UserNotFoundException;
import com.kipu.core.identity.domain.model.KycStatus;
import com.kipu.core.identity.domain.model.User;
import com.kipu.core.identity.domain.model.UserKyc;
import com.kipu.core.identity.domain.port.out.ContactProfileInfo;
import com.kipu.core.identity.domain.port.out.ProfileSyncPort;
import com.kipu.core.identity.domain.repository.UserKycRepository;
import com.kipu.core.identity.domain.repository.UserRepository;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompleteOnboardingUseCaseTest {

  @Mock private UserRepository userRepository;
  @Mock private ProfileSyncPort profileSyncPort;
  @Mock private UserKycRepository userKycRepository;

  @InjectMocks private CompleteOnboardingUseCase completeOnboardingUseCase;

  @Test
  @DisplayName("execute: Should complete onboarding successfully when user and kyc exist")
  void execute_ShouldCompleteOnboarding_WhenUserAndKycExist() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID selfContactId = UUID.randomUUID();
    String email = "dev@kipu.com";
    OffsetDateTime now = OffsetDateTime.now();

    CompleteOnboardingCommand command =
        new CompleteOnboardingCommand(userId, "Julian", "Miranda", LocalDate.of(1990, 1, 1));

    User user = mock(User.class);
    when(user.getId()).thenReturn(userId);
    when(user.getEmail()).thenReturn(email);
    when(user.isActive()).thenReturn(true);
    when(user.getCreatedAt()).thenReturn(now);

    UserKyc userKyc = mock(UserKyc.class);
    when(userKyc.getStatus()).thenReturn(KycStatus.PENDING);
    when(userKyc.isOnboardingCompleted()).thenReturn(false);

    ContactProfileInfo profileInfo = new ContactProfileInfo(selfContactId, "Julian", "Miranda");

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userKycRepository.findByUserId(userId)).thenReturn(Optional.of(userKyc));
    when(profileSyncPort.createSelfContact(any(), any(), any(), any(), any()))
        .thenReturn(profileInfo);

    // Act
    UserProfileResult result = completeOnboardingUseCase.execute(command);

    // Assert
    assertNotNull(result);
    assertEquals(userId, result.id());

    verify(userKyc).completeOnboarding(selfContactId);
    verify(userKycRepository).save(userKyc);
    verify(profileSyncPort)
        .createSelfContact(
            userId, command.firstName(), command.lastName(), email, command.birthdate());
  }

  @Test
  @DisplayName("execute: Should throw UserNotFoundException when user does not exist")
  void execute_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
    // Arrange
    UUID userId = UUID.randomUUID();
    CompleteOnboardingCommand command =
        new CompleteOnboardingCommand(userId, "Julian", "Miranda", LocalDate.of(1990, 1, 1));

    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(UserNotFoundException.class, () -> completeOnboardingUseCase.execute(command));
    verify(userKycRepository, never()).save(any());
  }

  @Test
  @DisplayName("execute: Should throw UserNotFoundException when kyc record does not exist")
  void execute_ShouldThrowUserNotFoundException_WhenKycDoesNotExist() {
    // Arrange
    UUID userId = UUID.randomUUID();
    CompleteOnboardingCommand command =
        new CompleteOnboardingCommand(userId, "Julian", "Miranda", LocalDate.of(1990, 1, 1));
    User user = mock(User.class);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userKycRepository.findByUserId(userId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(UserNotFoundException.class, () -> completeOnboardingUseCase.execute(command));
  }

  @Test
  @DisplayName("execute: Should throw ProfileSyncException when profile sync fails")
  void execute_ShouldThrowProfileSyncException_WhenSyncFails() {
    // Arrange
    UUID userId = UUID.randomUUID();
    String email = "dev@kipu.com";
    CompleteOnboardingCommand command =
        new CompleteOnboardingCommand(userId, "Julian", "Miranda", LocalDate.of(1990, 1, 1));

    User user = mock(User.class);
    when(user.getEmail()).thenReturn(email);
    UserKyc userKyc = mock(UserKyc.class);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userKycRepository.findByUserId(userId)).thenReturn(Optional.of(userKyc));
    when(profileSyncPort.createSelfContact(any(), any(), any(), any(), any()))
        .thenThrow(new RuntimeException("External Error"));

    // Act & Assert
    assertThrows(ProfileSyncException.class, () -> completeOnboardingUseCase.execute(command));
    verify(userKycRepository, never()).save(any());
  }
}
