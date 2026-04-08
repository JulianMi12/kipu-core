package com.kipu.core.contacts.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class UserTagNotFoundExceptionTest {

  @Test
  @DisplayName("Constructor: Should set correct message and status 404")
  void constructor_ShouldSetMessageAndStatus() {
    // Arrange
    UUID tagId = UUID.randomUUID();
    String expectedMessage = "UserTag not found with id: " + tagId;

    // Act
    UserTagNotFoundException exception = new UserTagNotFoundException(tagId);

    // Assert
    assertEquals(expectedMessage, exception.getMessage());
    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
  }
}
