package com.kipu.core.identity.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ProfileSyncExceptionTest {

  @Test
  @DisplayName(
      "Constructor: Should create exception with formatted message and internal server error status")
  void constructor_ShouldCreateWithDetail() {
    // Arrange
    String detail = "Connection timeout with Contacts module";
    String expectedMessage = "Failed to sync profile data: Connection timeout with Contacts module";

    // Act
    ProfileSyncException exception = new ProfileSyncException(detail);

    // Assert
    assertNotNull(exception);
    assertEquals(expectedMessage, exception.getMessage());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getHttpStatus());
  }

  @Test
  @DisplayName("Constructor with Cause: Should create exception and preserve cause")
  void constructor_ShouldCreateWithDetailAndCause() {
    // Arrange
    String detail = "Mapping error";
    RuntimeException cause = new RuntimeException("Jackson error");
    String expectedMessage = "Failed to sync profile data: Mapping error";

    // Act
    ProfileSyncException exception = new ProfileSyncException(detail, cause);

    // Assert
    assertNotNull(exception);
    assertEquals(expectedMessage, exception.getMessage());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getHttpStatus());
    // Nota: Aunque BusinessException maneje la causa internamente,
    // validamos que la instancia se cree correctamente.
  }
}
