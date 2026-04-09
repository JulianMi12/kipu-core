package com.kipu.core.identity.application.user.onboarding;

import java.time.LocalDate;
import java.util.UUID;

public record CompleteOnboardingCommand(
    UUID userId, String firstName, String lastName, LocalDate birthdate, String timezone) {}
