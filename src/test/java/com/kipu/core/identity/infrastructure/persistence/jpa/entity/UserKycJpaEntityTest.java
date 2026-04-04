package com.kipu.core.identity.infrastructure.persistence.jpa.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.kipu.core.identity.domain.model.KycStatus;
import com.kipu.core.identity.domain.model.UserKyc;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserKycJpaEntityTest {

  @Test
  void fromDomain_ShouldMapAllFieldsCorrectly() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID selfContactId = UUID.randomUUID();
    LocalDate birthdate = LocalDate.of(1990, 1, 1);
    OffsetDateTime now = OffsetDateTime.now();

    UserKyc domain = UserKyc.reconstitute(
        userId, selfContactId, KycStatus.COMPLETED, true, birthdate, now
    );

    // Act
    UserKycJpaEntity entity = UserKycJpaEntity.fromDomain(domain);

    // Assert
    assertThat(entity.getUserId()).isEqualTo(userId);
    assertThat(entity.getSelfContactId()).isEqualTo(selfContactId);
    assertThat(entity.getStatus()).isEqualTo(KycStatus.COMPLETED);
    assertThat(entity.isOnboardingCompleted()).isTrue();
    assertThat(entity.getBirthdate()).isEqualTo(birthdate);
    assertThat(entity.getUpdatedAt()).isEqualTo(now);
  }

  @Test
  void toDomain_ShouldMapAllFieldsCorrectly() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID selfContactId = UUID.randomUUID();
    LocalDate birthdate = LocalDate.of(1985, 5, 20);
    OffsetDateTime now = OffsetDateTime.now();

    UserKycJpaEntity entity = new UserKycJpaEntity(
        userId, selfContactId, KycStatus.IN_PROGRESS, false, birthdate, now
    );

    // Act
    UserKyc domain = entity.toDomain();

    // Assert
    assertThat(domain.getUserId()).isEqualTo(userId);
    assertThat(domain.getSelfContactId()).isEqualTo(selfContactId);
    assertThat(domain.getStatus()).isEqualTo(KycStatus.IN_PROGRESS);
    assertThat(domain.isOnboardingCompleted()).isFalse();
    assertThat(domain.getBirthdate()).isEqualTo(birthdate);
    assertThat(domain.getUpdatedAt()).isEqualTo(now);
  }
}