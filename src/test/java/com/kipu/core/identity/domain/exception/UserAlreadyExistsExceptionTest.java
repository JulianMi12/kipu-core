package com.kipu.core.identity.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class UserAlreadyExistsExceptionTest {

  @Test
  @DisplayName("Should initialize exception with correct message, status and metadata")
  void shouldInitializeCorrectly() {
    // Arrange
    String email = "duplicate@kipu.com";

    // Act
    UserAlreadyExistsException exception = new UserAlreadyExistsException(email);

    // Assert
    assertThat(exception.getMessage())
        .isEqualTo("User with email 'duplicate@kipu.com' already exists");

    assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);

    assertThat(exception.getEmail()).isEqualTo(email);
  }

  @Test
  @DisplayName("Should be instance of BusinessException")
  void shouldBeInstanceOfBusinessException() {
    UserAlreadyExistsException exception = new UserAlreadyExistsException("test@kipu.com");

    assertThat(exception)
        .isInstanceOf(com.kipu.core.common.domain.exception.BusinessException.class);
  }
}
