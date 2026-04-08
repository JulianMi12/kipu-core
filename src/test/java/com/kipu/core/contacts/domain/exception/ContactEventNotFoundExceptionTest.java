package com.kipu.core.contacts.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ContactEventNotFoundExceptionTest {

  @Test
  void constructor_ShouldSetCorrectMessageAndStatus_WhenEventIdIsProvided() {
    // Arrange
    UUID eventId = UUID.randomUUID();
    String expectedMessage = "Contact event not found with id: " + eventId;

    // Act
    ContactEventNotFoundException exception = new ContactEventNotFoundException(eventId);

    // Assert
    assertThat(exception.getMessage()).isEqualTo(expectedMessage);
    // Nota: Asumo que BusinessException expone el getter getHttpStatus().
    // Si tu clase base usa otro nombre como getStatus(), solo ajusta esta línea.
    assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
