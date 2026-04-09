package com.kipu.core.identity.application.user.onboarding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

  private static final String DEFAULT_TIMEZONE = "UTC";

  @Test
  @DisplayName("execute: Should complete onboarding successfully and sync profile with timezone")
  void execute_ShouldCompleteOnboarding_WhenUserAndKycExist() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID selfContactId = UUID.randomUUID();
    String email = "dev@kipu.com";
    OffsetDateTime now = OffsetDateTime.now();
    String timezone = "America/Bogota";

    CompleteOnboardingCommand command =
        new CompleteOnboardingCommand(
            userId, "Julian", "Miranda", LocalDate.of(1990, 1, 1), timezone);

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
    when(profileSyncPort.createSelfContact(any(), any(), any(), any(), any(), anyString()))
        .thenReturn(profileInfo);

    // Act
    UserProfileResult result = completeOnboardingUseCase.execute(command);

    // Assert
    assertNotNull(result);
    assertEquals(userId, result.id());
    assertEquals(KycStatus.PENDING, result.kycStatus());

    verify(userKyc).completeOnboarding(selfContactId);
    verify(userKycRepository).save(userKyc);
    verify(profileSyncPort)
        .createSelfContact(eq(userId), eq("Julian"), eq("Miranda"), eq(email), any(), eq(timezone));
  }

  @Test
  @DisplayName("execute: Should throw UserNotFoundException when user does not exist")
  void execute_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
    // Arrange
    UUID userId = UUID.randomUUID();
    CompleteOnboardingCommand command =
        new CompleteOnboardingCommand(userId, "J", "M", LocalDate.now(), DEFAULT_TIMEZONE);

    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(UserNotFoundException.class, () -> completeOnboardingUseCase.execute(command));
    verify(userKycRepository, never()).findByUserId(any());
    verify(profileSyncPort, never()).createSelfContact(any(), any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName(
      "execute: Should throw UserNotFoundException when KYC record is missing (Branch Coverage)")
  void execute_ShouldThrowUserNotFoundException_WhenKycIsMissing() {
    // Arrange
    UUID userId = UUID.randomUUID();
    CompleteOnboardingCommand command =
        new CompleteOnboardingCommand(
            userId, "Julian", "Miranda", LocalDate.now(), DEFAULT_TIMEZONE);

    when(userRepository.findById(userId)).thenReturn(Optional.of(mock(User.class)));
    // El usuario existe, pero el registro de KYC falla (orElseThrow en getUserKyc)
    when(userKycRepository.findByUserId(userId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(UserNotFoundException.class, () -> completeOnboardingUseCase.execute(command));
    verify(profileSyncPort, never()).createSelfContact(any(), any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("execute: Should throw ProfileSyncException and roll back when profile sync fails")
  void execute_ShouldThrowProfileSyncException_WhenSyncFails() {
    // Arrange
    UUID userId = UUID.randomUUID();
    CompleteOnboardingCommand command =
        new CompleteOnboardingCommand(
            userId, "Julian", "Miranda", LocalDate.now(), DEFAULT_TIMEZONE);

    User user = mock(User.class);
    when(user.getEmail()).thenReturn("dev@kipu.com");
    UserKyc userKyc = mock(UserKyc.class);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userKycRepository.findByUserId(userId)).thenReturn(Optional.of(userKyc));

    when(profileSyncPort.createSelfContact(any(), any(), any(), any(), any(), any()))
        .thenThrow(new RuntimeException("External Service Down"));

    // Act & Assert
    assertThrows(ProfileSyncException.class, () -> completeOnboardingUseCase.execute(command));

    // Si falla el sync, no se guarda el KYC
    verify(userKycRepository, never()).save(any());
  }

  private String anyString() {
    return any(String.class);
  }
}
