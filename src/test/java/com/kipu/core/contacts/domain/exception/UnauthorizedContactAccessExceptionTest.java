package com.kipu.core.contacts.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class UnauthorizedContactAccessExceptionTest {

  @Test
  void constructor_ShouldSetCorrectMessageAndStatus() {
    // Arrange
    String expectedMessage = "Access denied: you do not own this contact.";

    // Act
    UnauthorizedContactAccessException exception = new UnauthorizedContactAccessException();

    // Assert
    assertThat(exception.getMessage()).isEqualTo(expectedMessage);
    assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
  }
}
