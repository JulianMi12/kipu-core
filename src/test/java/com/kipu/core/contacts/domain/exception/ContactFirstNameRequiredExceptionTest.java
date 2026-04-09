package com.kipu.core.contacts.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ContactFirstNameRequiredExceptionTest {

  @Test
  @DisplayName("Should create exception with correct message and status")
  void shouldCreateExceptionWithCorrectMessageAndStatus() {
    // Act
    ContactFirstNameRequiredException exception = new ContactFirstNameRequiredException();

    // Assert
    assertThat(exception.getMessage())
        .isEqualTo("Contact first name is required and cannot be empty");

    assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  @DisplayName("Should be an instance of BusinessException")
  void shouldBeInstanceOfBusinessException() {
    // Act
    ContactFirstNameRequiredException exception = new ContactFirstNameRequiredException();

    // Assert
    assertThat(exception)
        .isInstanceOf(com.kipu.core.common.domain.exception.BusinessException.class);
  }
}
