package com.kipu.core.identity.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class UserTest {

  @Nested
  @DisplayName("Factory Method: create")
  class CreateTests {

    @Test
    void shouldCreateUser_WhenDataIsValid() {
      // Arrange
      String email = "  Kipu@Core.com  ";
      String hash = "secure-hash";

      // Act
      User user = User.create(email, hash);

      // Assert
      assertThat(user.getId()).isNotNull();
      assertThat(user.getEmail()).isEqualTo("kipu@core.com");
      assertThat(user.isActive()).isTrue();
      assertThat(user.getCreatedAt()).isNotNull();
    }

    @ParameterizedTest
    @DisplayName("Should throw exception for empty or blank email")
    @ValueSource(strings = {"", "   "})
    void shouldThrowException_WhenEmailIsBlank(String email) {
      assertThatThrownBy(() -> User.create(email, "hash"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("El email del usuario no puede estar vacío.");
    }

    @ParameterizedTest
    @DisplayName("Should throw exception for malformed email")
    @ValueSource(strings = {"not-an-email", "@kipu.com", "test@", "@@@", "test @kipu.com"})
    void shouldThrowException_WhenEmailFormatIsInvalid(String email) {
      assertThatThrownBy(() -> User.create(email, "hash"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("El email del usuario no tiene un formato válido.");
    }

    @Test
    void shouldThrowException_WhenEmailIsNull() {
      assertThatThrownBy(() -> User.create(null, "hash"))
          .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @DisplayName("Should throw exception for empty or blank password hash")
    @ValueSource(strings = {"", "   "})
    void shouldThrowException_WhenPasswordHashIsBlank(String hash) {
      assertThatThrownBy(() -> User.create("test@kipu.com", hash))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("El hash de contraseña no puede estar vacío.");
    }
  }

  @Nested
  @DisplayName("Method: reconstitute")
  class ReconstituteTests {
    @Test
    void shouldReconstituteUser_WithProvidedData() {
      UUID id = UUID.randomUUID();
      OffsetDateTime now = OffsetDateTime.now();

      User user = User.reconstitute(id, "test@kipu.com", "hash", false, now);

      assertThat(user.getId()).isEqualTo(id);
      assertThat(user.isActive()).isFalse();
      assertThat(user.getCreatedAt()).isEqualTo(now);
    }
  }

  @Nested
  @DisplayName("Domain Behaviour: activation/deactivation")
  class ActivationTests {

    @Test
    void deactivate_ShouldChangeStatus_WhenIsActive() {
      User user = User.create("test@kipu.com", "hash");

      user.deactivate();

      assertThat(user.isActive()).isFalse();
    }

    @Test
    void deactivate_ShouldThrowException_WhenAlreadyInactive() {
      User user = User.reconstitute(UUID.randomUUID(), "a@b.com", "h", false, OffsetDateTime.now());

      assertThatThrownBy(user::deactivate)
          .isInstanceOf(IllegalStateException.class)
          .hasMessage("El usuario ya está inactivo.");
    }

    @Test
    void activate_ShouldChangeStatus_WhenIsInactive() {
      User user = User.reconstitute(UUID.randomUUID(), "a@b.com", "h", false, OffsetDateTime.now());

      user.activate();

      assertThat(user.isActive()).isTrue();
    }

    @Test
    void activate_ShouldThrowException_WhenAlreadyActive() {
      User user = User.create("test@kipu.com", "hash");

      assertThatThrownBy(user::activate)
          .isInstanceOf(IllegalStateException.class)
          .hasMessage("El usuario ya está activo.");
    }
  }

  @Nested
  @DisplayName("Domain Behaviour: Password Management")
  class PasswordTests {
    @Test
    void changePasswordHash_ShouldUpdateValue_WhenValid() {
      User user = User.create("test@kipu.com", "old-hash");

      user.changePasswordHash("new-hash");

      assertThat(user.getPasswordHash()).isEqualTo("new-hash");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void changePasswordHash_ShouldThrowException_WhenInvalid(String newHash) {
      User user = User.create("test@kipu.com", "hash");

      assertThatThrownBy(() -> user.changePasswordHash(newHash))
          .isInstanceOf(IllegalArgumentException.class);
    }
  }

  @Test
  @DisplayName("Should throw exception when password hash is null")
  void shouldThrowException_WhenPasswordHashIsNull() {
    // Arrange
    String email = "test@kipu.com";
    String nullHash = null;

    // Act & Assert
    assertThatThrownBy(() -> User.create(email, nullHash))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El hash de contraseña no puede estar vacío.");
  }

  @Test
  @DisplayName("changePasswordHash: Should throw exception when new hash is null")
  void changePasswordHash_ShouldThrowException_WhenNull() {
    // Arrange
    User user =
        User.reconstitute(
            UUID.randomUUID(), "test@kipu.com", "old-hash", true, OffsetDateTime.now());

    // Act & Assert
    assertThatThrownBy(() -> user.changePasswordHash(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("El nuevo hash de contraseña no puede estar vacío.");
  }
}
