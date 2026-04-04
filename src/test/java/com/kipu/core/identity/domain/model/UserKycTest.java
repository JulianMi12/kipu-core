package com.kipu.core.identity.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserKycTest {

  @Test
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
    assertThat(kyc.getBirthdate()).isNull();
    assertThat(kyc.getUpdatedAt()).isAfterOrEqualTo(beforeCreation);
  }

  @Test
  void reconstitute_ShouldRestoreAllFieldsExactly() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID selfContactId = UUID.randomUUID();
    KycStatus status = KycStatus.IN_PROGRESS;
    boolean onboardingCompleted = false;
    LocalDate birthdate = LocalDate.of(1990, 5, 15);
    OffsetDateTime updatedAt = OffsetDateTime.now();

    // Act
    UserKyc kyc = UserKyc.reconstitute(
        userId,
        selfContactId,
        status,
        onboardingCompleted,
        birthdate,
        updatedAt
    );

    // Assert
    assertThat(kyc.getUserId()).isEqualTo(userId);
    assertThat(kyc.getSelfContactId()).isEqualTo(selfContactId);
    assertThat(kyc.getStatus()).isEqualTo(status);
    assertThat(kyc.isOnboardingCompleted()).isEqualTo(onboardingCompleted);
    assertThat(kyc.getBirthdate()).isEqualTo(birthdate);
    assertThat(kyc.getUpdatedAt()).isEqualTo(updatedAt);
  }
}