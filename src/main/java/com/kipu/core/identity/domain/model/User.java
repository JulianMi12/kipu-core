package com.kipu.core.identity.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class User {

  private final UUID id;
  private final String email;
  private String passwordHash;
  private boolean active;
  private final OffsetDateTime createdAt;

  public static User create(String email, String passwordHash) {
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("El email del usuario no puede estar vacío.");
    }

    String cleanEmail = email.strip().toLowerCase();

    if (cleanEmail.contains(" ")
        || !cleanEmail.contains("@")
        || cleanEmail.startsWith("@")
        || cleanEmail.endsWith("@")) {
      throw new IllegalArgumentException("El email del usuario no tiene un formato válido.");
    }

    if (passwordHash == null || passwordHash.isBlank()) {
      throw new IllegalArgumentException("El hash de contraseña no puede estar vacío.");
    }

    return new User(UUID.randomUUID(), cleanEmail, passwordHash, true, OffsetDateTime.now());
  }

  public static User reconstitute(
      UUID id, String email, String passwordHash, boolean active, OffsetDateTime createdAt) {
    return new User(id, email, passwordHash, active, createdAt);
  }

  public void deactivate() {
    if (!this.active) {
      throw new IllegalStateException("El usuario ya está inactivo.");
    }
    this.active = false;
  }

  public void activate() {
    if (this.active) {
      throw new IllegalStateException("El usuario ya está activo.");
    }
    this.active = true;
  }

  public void changePasswordHash(String newPasswordHash) {
    if (newPasswordHash == null || newPasswordHash.isBlank()) {
      throw new IllegalArgumentException("El nuevo hash de contraseña no puede estar vacío.");
    }
    this.passwordHash = newPasswordHash;
  }
}
