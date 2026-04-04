package com.kipu.core.identity.application.registration;

import com.kipu.core.identity.domain.model.AuthTokens;
import java.util.UUID;

public record RegistrationResult(UUID userId, String email, AuthTokens tokens) {}
