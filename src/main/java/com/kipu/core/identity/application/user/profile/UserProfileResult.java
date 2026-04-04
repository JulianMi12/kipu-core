package com.kipu.core.identity.application.user.profile;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserProfileResult(UUID id, String email, boolean active, OffsetDateTime createdAt) {}
