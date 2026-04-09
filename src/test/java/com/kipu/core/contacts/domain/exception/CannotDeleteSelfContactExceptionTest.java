package com.kipu.core.contacts.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.kipu.core.common.domain.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class CannotDeleteSelfContactExceptionTest {

  @Test
  @DisplayName("Should create exception with 409 Conflict and specific message")
  void shouldCreateExceptionWithCorrectMessageAndStatus() {
    // Act
    CannotDeleteSelfContactException exception = new CannotDeleteSelfContactException();

    // Assert
    assertThat(exception.getMessage()).isEqualTo("Cannot delete the self-contact of a user");

    assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  @DisplayName("Should be an instance of BusinessException for global handling")
  void shouldBeInstanceOfBusinessException() {
    // Act
    CannotDeleteSelfContactException exception = new CannotDeleteSelfContactException();

    // Assert
    assertThat(exception).isInstanceOf(BusinessException.class);
  }
}
