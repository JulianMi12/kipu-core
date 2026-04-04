package com.kipu.core.identity.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class InvalidCredentialsExceptionTest {

  @Test
  void constructor_ShouldSetCorrectMessageAndStatus() {
    // Act
    InvalidCredentialsException exception = new InvalidCredentialsException();

    // Assert
    assertThat(exception.getMessage()).isEqualTo("Invalid email or password");
    assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void shouldBeInstanceOfBusinessException() {
    // Act
    InvalidCredentialsException exception = new InvalidCredentialsException();

    // Assert
    assertThat(exception)
        .isInstanceOf(com.kipu.core.common.domain.exception.BusinessException.class);
  }
}
