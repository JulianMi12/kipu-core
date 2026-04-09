package com.kipu.core.identity.application.user.profile;

import com.kipu.core.identity.domain.model.KycStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record UserProfileResult(
    UUID id,
    String email,
    boolean active,
    OffsetDateTime createdAt,
    KycStatus kycStatus,
    boolean onboardingCompleted,
    String firstName,
    String lastName,
    UUID selfContactId) {}
