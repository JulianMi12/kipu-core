package com.kipu.core.identity.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserKycTest {

  @Test
  @DisplayName("createPending: Should initialize with PENDING status and false onboarding")
  void createPending_ShouldInitializeWithCorrectDefaults() {
    // Arrange
    UUID userId = UUID.randomUUID();
    OffsetDateTime beforeCreation = OffsetDateTime.now().minusSeconds(1);

    // Act
    UserKyc kyc = UserKyc.createPending(userId);

    // Assert
    assertThat(kyc.getUserId()).isEqualTo(userId);
    assertThat(kyc.getSelfContactId()).isNull();
    assertThat(kyc.getStatus()).isEqualTo(KycStatus.PENDING);
    assertThat(kyc.isOnboardingCompleted()).isFalse();
    assertThat(kyc.getUpdatedAt()).isAfterOrEqualTo(beforeCreation);
  }

  @Test
  @DisplayName("reconstitute: Should restore all fields exactly as provided")
  void reconstitute_ShouldRestoreAllFieldsExactly() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID selfContactId = UUID.randomUUID();
    KycStatus status = KycStatus.IN_PROGRESS;
    boolean onboardingCompleted = false;
    OffsetDateTime updatedAt = OffsetDateTime.now();

    // Act
    UserKyc kyc =
        UserKyc.reconstitute(userId, selfContactId, status, onboardingCompleted, updatedAt);

    // Assert
    assertThat(kyc.getUserId()).isEqualTo(userId);
    assertThat(kyc.getSelfContactId()).isEqualTo(selfContactId);
    assertThat(kyc.getStatus()).isEqualTo(status);
    assertThat(kyc.isOnboardingCompleted()).isEqualTo(onboardingCompleted);
    assertThat(kyc.getUpdatedAt()).isEqualTo(updatedAt);
  }

  @Test
  @DisplayName("completeOnboarding: Should update status to COMPLETED and set selfContactId")
  void completeOnboarding_ShouldUpdateStateCorrectively() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID selfContactId = UUID.randomUUID();
    UserKyc kyc = UserKyc.createPending(userId);
    OffsetDateTime beforeUpdate = OffsetDateTime.now().minusSeconds(1);

    // Act
    kyc.completeOnboarding(selfContactId);

    // Assert
    assertThat(kyc.getSelfContactId()).isEqualTo(selfContactId);
    assertThat(kyc.getStatus()).isEqualTo(KycStatus.COMPLETED);
    assertThat(kyc.isOnboardingCompleted()).isTrue();
    assertThat(kyc.getUpdatedAt()).isAfterOrEqualTo(beforeUpdate);
  }
}
