package com.kipu.core.identity.application.user.profile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.identity.domain.exception.UserNotFoundException;
import com.kipu.core.identity.domain.model.KycStatus;
import com.kipu.core.identity.domain.model.User;
import com.kipu.core.identity.domain.model.UserKyc;
import com.kipu.core.identity.domain.port.out.ContactProfileInfo;
import com.kipu.core.identity.domain.port.out.ProfileSyncPort;
import com.kipu.core.identity.domain.repository.UserKycRepository;
import com.kipu.core.identity.domain.repository.UserRepository;
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
class GetUserProfileUseCaseTest {

  @Mock private UserRepository userRepository;
  @Mock private UserKycRepository userKycRepository;
  @Mock private ProfileSyncPort profileSyncPort;

  @InjectMocks private GetUserProfileUseCase getUserProfileUseCase;

  @Test
  @DisplayName("Should return basic profile when KYC/Onboarding is not started")
  void execute_ShouldReturnProfile_WhenUserExistsButNoKyc() {
    // Arrange
    UUID userId = UUID.randomUUID();
    OffsetDateTime now = OffsetDateTime.now();
    User mockUser = User.reconstitute(userId, "test@kipu.com", "hash123", true, now);

    when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
    when(userKycRepository.findByUserId(userId)).thenReturn(Optional.empty());

    // Act
    UserProfileResult result = getUserProfileUseCase.execute(userId);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.kycStatus()).isEqualTo(KycStatus.PENDING);
    assertThat(result.onboardingCompleted()).isFalse();
    assertThat(result.firstName()).isNull();
    verify(userRepository).findById(userId);
    verify(userKycRepository).findByUserId(userId);
  }

  @Test
  @DisplayName("Should return full profile with names when onboarding is completed")
  void execute_ShouldReturnFullProfile_WhenOnboardingIsCompleted() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    User mockUser = User.reconstitute(userId, "test@kipu.com", "hash", true, OffsetDateTime.now());

    // Mock KYC
    UserKyc mockKyc =
        UserKyc.reconstitute(userId, contactId, KycStatus.COMPLETED, true, OffsetDateTime.now());

    // Mock Contact Info
    ContactProfileInfo contactInfo = new ContactProfileInfo(contactId, "Julian", "Miranda");

    when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
    when(userKycRepository.findByUserId(userId)).thenReturn(Optional.of(mockKyc));
    when(profileSyncPort.getContactById(contactId)).thenReturn(Optional.of(contactInfo));

    // Act
    UserProfileResult result = getUserProfileUseCase.execute(userId);

    // Assert
    assertThat(result.onboardingCompleted()).isTrue();
    assertThat(result.firstName()).isEqualTo("Julian");
    assertThat(result.lastName()).isEqualTo("Miranda");
    verify(profileSyncPort).getContactById(contactId);
  }

  @Test
  @DisplayName("Should return null names if profile sync fails even if onboarding is completed")
  void execute_ShouldReturnNullNames_WhenContactServiceReturnsEmpty() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    User mockUser = User.reconstitute(userId, "test@kipu.com", "hash", true, OffsetDateTime.now());

    UserKyc mockKyc =
        UserKyc.reconstitute(userId, contactId, KycStatus.COMPLETED, true, OffsetDateTime.now());

    when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
    when(userKycRepository.findByUserId(userId)).thenReturn(Optional.of(mockKyc));
    when(profileSyncPort.getContactById(contactId)).thenReturn(Optional.empty());

    // Act
    UserProfileResult result = getUserProfileUseCase.execute(userId);

    // Assert
    assertThat(result.onboardingCompleted()).isTrue();
    assertThat(result.firstName()).isNull();
    assertThat(result.lastName()).isNull();
  }

  @Test
  void execute_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
    // Arrange
    UUID userId = UUID.randomUUID();
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> getUserProfileUseCase.execute(userId))
        .isInstanceOf(UserNotFoundException.class);

    verify(userRepository).findById(userId);
  }
}
