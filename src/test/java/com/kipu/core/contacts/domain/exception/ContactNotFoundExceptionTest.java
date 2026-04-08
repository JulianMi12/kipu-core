package com.kipu.core.contacts.domain.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ContactNotFoundExceptionTest {

  @Test
  void constructor_ShouldSetCorrectMessageAndStatus_WhenContactIdIsProvided() {
    // Arrange
    UUID contactId = UUID.randomUUID();
    String expectedMessage = "Contact not found with id: " + contactId;

    // Act
    ContactNotFoundException exception = new ContactNotFoundException(contactId);

    // Assert
    assertThat(exception.getMessage()).isEqualTo(expectedMessage);
    // Nota: Igual que en el test anterior, asegúrate de que getHttpStatus()
    // sea el nombre correcto del método en tu clase base BusinessException.
    assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
