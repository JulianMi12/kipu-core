package com.kipu.core.identity.infrastructure.rest.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegistrationResponse {
  private final UUID userId;
  private final String email;
  private final String accessToken;
  private final String refreshToken;
  private final String tokenType;
}
