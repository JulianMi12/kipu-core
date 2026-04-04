package com.kipu.core.identity.domain.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserKyc {

  private final UUID userId;
  private UUID selfContactId;
  private KycStatus status;
  private boolean onboardingCompleted;
  private LocalDate birthdate;
  private OffsetDateTime updatedAt;

  public static UserKyc createPending(UUID userId) {
    return new UserKyc(userId, null, KycStatus.PENDING, false, null, OffsetDateTime.now());
  }

  public static UserKyc reconstitute(
      UUID userId,
      UUID selfContactId,
      KycStatus status,
      boolean onboardingCompleted,
      LocalDate birthdate,
      OffsetDateTime updatedAt) {
    return new UserKyc(userId, selfContactId, status, onboardingCompleted, birthdate, updatedAt);
  }
}
