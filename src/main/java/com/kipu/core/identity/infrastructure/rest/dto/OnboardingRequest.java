package com.kipu.core.identity.infrastructure.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;

public record OnboardingRequest(
    @NotBlank String firstName,
    @NotBlank String lastName,
    @NotNull @Past LocalDate birthdate,
    @NotBlank String timezone) {}
