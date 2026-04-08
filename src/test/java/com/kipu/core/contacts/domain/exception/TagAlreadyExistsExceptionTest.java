package com.kipu.core.contacts.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class TagAlreadyExistsExceptionTest {

  @Test
  @DisplayName("Constructor: Should format message correctly and set HttpStatus to CONFLICT")
  void constructor_ShouldSetCorrectMessageAndStatus() {
    // Arrange
    String tagName = "trabajo";
    String expectedMessage = "Tag with name 'trabajo' already exists for this user.";

    // Act
    TagAlreadyExistsException exception = new TagAlreadyExistsException(tagName);

    // Assert
    assertEquals(expectedMessage, exception.getMessage());
    assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
  }
}
