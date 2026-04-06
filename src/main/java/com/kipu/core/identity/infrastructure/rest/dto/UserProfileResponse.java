package com.kipu.core.identity.infrastructure.rest.dto;

import com.kipu.core.identity.domain.model.KycStatus;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileResponse {
  private final UUID id;
  private final String email;
  private final boolean active;
  private final OffsetDateTime createdAt;
  private final KycStatus kycStatus;
  private final boolean onboardingCompleted;
  private final String firstName;
  private final String lastName;
}
