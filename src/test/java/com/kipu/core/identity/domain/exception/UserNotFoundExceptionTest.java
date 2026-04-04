package com.kipu.core.identity.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.kipu.core.common.domain.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class UserNotFoundExceptionTest {

  @Test
  @DisplayName("Should initialize with default message and NOT_FOUND status")
  void shouldInitializeWithDefaultValues() {
    // Act
    UserNotFoundException exception = new UserNotFoundException();

    // Assert
    assertThat(exception.getMessage()).isEqualTo("User not found.");

    assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  @DisplayName("Should be an instance of BusinessException for global handling")
  void shouldInheritFromBusinessException() {
    // Act
    UserNotFoundException exception = new UserNotFoundException();

    // Assert
    assertThat(exception).isInstanceOf(BusinessException.class);
  }
}
