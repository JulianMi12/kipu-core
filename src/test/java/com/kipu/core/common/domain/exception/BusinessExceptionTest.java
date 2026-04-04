package com.kipu.core.common.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class BusinessExceptionTest {

  private static class TestException extends BusinessException {
    protected TestException(String message, HttpStatus httpStatus) {
      super(message, httpStatus);
    }
  }

  @Test
  void constructor_ShouldSetMessageAndHttpStatus() {
    // Arrange
    String expectedMessage = "Error de negocio de prueba";
    HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;

    // Act
    TestException exception = new TestException(expectedMessage, expectedStatus);

    // Assert
    assertThat(exception.getMessage()).isEqualTo(expectedMessage);
    assertThat(exception.getHttpStatus()).isEqualTo(expectedStatus);
  }

  @Test
  void businessException_ShouldBeInstanceOfRuntimeException() {
    // Act
    TestException exception = new TestException("Error", HttpStatus.INTERNAL_SERVER_ERROR);

    // Assert
    assertThat(exception).isInstanceOf(RuntimeException.class);
  }
}
