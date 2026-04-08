package com.kipu.core.contacts.infrastructure.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserTagRequest(
    @NotBlank @Size(max = 50) String name,
    @NotBlank @Pattern(regexp = "^#[0-9A-Fa-f]{6}$") String colorHex) {}
