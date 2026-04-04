package com.kipu.core.identity.infrastructure.rest.dto;

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
}
