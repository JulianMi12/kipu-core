package com.kipu.core.identity.application.auth.login;

import com.kipu.core.identity.domain.model.AuthTokens;
import java.util.UUID;

public record LoginResult(UUID userId, String email, AuthTokens tokens) {}
