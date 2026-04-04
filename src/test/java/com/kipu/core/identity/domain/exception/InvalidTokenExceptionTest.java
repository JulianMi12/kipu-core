package com.kipu.core.identity.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class InvalidTokenExceptionTest {

  @Test
  void constructor_ShouldSetCustomMessageAndUnauthorizedStatus() {
    // Arrange
    String customMessage = "Token has expired or is malformed";

    // Act
    InvalidTokenException exception = new InvalidTokenException(customMessage);

    // Assert
    assertThat(exception.getMessage()).isEqualTo(customMessage);
    assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void shouldBeInstanceOfBusinessException() {
    // Act
    InvalidTokenException exception = new InvalidTokenException("Any message");

    // Assert
    assertThat(exception)
        .isInstanceOf(com.kipu.core.common.domain.exception.BusinessException.class);
  }
}
