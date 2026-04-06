package com.kipu.core.identity.infrastructure.persistence.jpa.entity;

import com.kipu.core.identity.domain.model.KycStatus;
import com.kipu.core.identity.domain.model.UserKyc;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_kyc", schema = "identity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserKycJpaEntity {

  @Id
  @Column(name = "user_id", nullable = false, updatable = false)
  private UUID userId;

  @Column(name = "self_contact_id")
  private UUID selfContactId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private KycStatus status;

  @Column(name = "onboarding_completed", nullable = false)
  private boolean onboardingCompleted;

  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  public static UserKycJpaEntity fromDomain(UserKyc userKyc) {
    return new UserKycJpaEntity(
        userKyc.getUserId(),
        userKyc.getSelfContactId(),
        userKyc.getStatus(),
        userKyc.isOnboardingCompleted(),
        userKyc.getUpdatedAt());
  }

  public UserKyc toDomain() {
    return UserKyc.reconstitute(userId, selfContactId, status, onboardingCompleted, updatedAt);
  }
}
