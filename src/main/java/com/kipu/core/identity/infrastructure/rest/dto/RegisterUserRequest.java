package com.kipu.core.identity.infrastructure.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
    @NotBlank(message = "El email es obligatorio.")
        @Email(message = "El email no tiene un formato válido.")
        String email,
    @NotBlank(message = "La contraseña es obligatoria.")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
        String password) {}
