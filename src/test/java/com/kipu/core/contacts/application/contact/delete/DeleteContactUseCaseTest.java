package com.kipu.core.contacts.application.contact.delete;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.domain.exception.CannotDeleteSelfContactException;
import com.kipu.core.contacts.domain.exception.ContactNotFoundException;
import com.kipu.core.contacts.domain.exception.UnauthorizedContactAccessException;
import com.kipu.core.contacts.domain.model.Contact;
import com.kipu.core.contacts.domain.port.out.SelfContactLookupPort;
import com.kipu.core.contacts.domain.repository.ContactRepository;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteContactUseCaseTest {

  @Mock private ContactRepository contactRepository;
  @Mock private SelfContactLookupPort selfContactLookupPort;

  @InjectMocks private DeleteContactUseCase deleteContactUseCase;

  @Test
  @DisplayName("execute: Should delete contact when all validations pass")
  void execute_ShouldDelete_WhenValid() {
    // Arrange
    UUID contactId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    UUID differentSelfId = UUID.randomUUID();

    Contact contact = createTestContact(contactId, userId);
    DeleteContactCommand command = new DeleteContactCommand(userId, contactId);

    when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));
    when(selfContactLookupPort.findSelfContactId(userId)).thenReturn(Optional.of(differentSelfId));

    // Act
    deleteContactUseCase.execute(command);

    // Assert
    verify(contactRepository).delete(contactId);
  }

  @Test
  @DisplayName("execute: Should throw ContactNotFoundException when contact does not exist")
  void execute_ShouldThrowNotFound_WhenIdDoesNotExist() {
    // Arrange
    UUID contactId = UUID.randomUUID();
    DeleteContactCommand command = new DeleteContactCommand(UUID.randomUUID(), contactId);

    when(contactRepository.findById(contactId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> deleteContactUseCase.execute(command))
        .isInstanceOf(ContactNotFoundException.class);

    verify(contactRepository, never()).delete(any());
  }

  @Test
  @DisplayName(
      "execute: Should throw UnauthorizedContactAccessException when user is not the owner")
  void execute_ShouldThrowUnauthorized_WhenUserIsNotOwner() {
    // Arrange
    UUID contactId = UUID.randomUUID();
    UUID realOwnerId = UUID.randomUUID();
    UUID maliciousUserId = UUID.randomUUID();

    Contact contact = createTestContact(contactId, realOwnerId);
    DeleteContactCommand command = new DeleteContactCommand(maliciousUserId, contactId);

    when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));

    // Act & Assert
    assertThatThrownBy(() -> deleteContactUseCase.execute(command))
        .isInstanceOf(UnauthorizedContactAccessException.class);

    verify(contactRepository, never()).delete(any());
  }

  @Test
  @DisplayName(
      "execute: Should throw CannotDeleteSelfContactException when trying to delete own profile contact")
  void execute_ShouldThrowCannotDeleteSelf_WhenIdsMatch() {
    // Arrange
    UUID contactId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    Contact contact = createTestContact(contactId, userId);
    DeleteContactCommand command = new DeleteContactCommand(userId, contactId);

    when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));
    // Simulamos que el contacto que se quiere borrar ES el contacto propio del usuario
    when(selfContactLookupPort.findSelfContactId(userId)).thenReturn(Optional.of(contactId));

    // Act & Assert
    assertThatThrownBy(() -> deleteContactUseCase.execute(command))
        .isInstanceOf(CannotDeleteSelfContactException.class);

    verify(contactRepository, never()).delete(any());
  }

  @Test
  @DisplayName("execute: Should delete contact when selfContactId lookup returns empty")
  void execute_ShouldDelete_WhenSelfContactNotFound() {
    // Arrange
    UUID contactId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    Contact contact = createTestContact(contactId, userId);
    DeleteContactCommand command = new DeleteContactCommand(userId, contactId);

    when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));
    when(selfContactLookupPort.findSelfContactId(userId)).thenReturn(Optional.empty());

    // Act
    deleteContactUseCase.execute(command);

    // Assert
    verify(contactRepository).delete(contactId);
  }

  private Contact createTestContact(UUID id, UUID ownerId) {
    return Contact.reconstitute(
        id,
        ownerId,
        "Test",
        "User",
        "test@test.com",
        null,
        Map.of(),
        Set.of(),
        OffsetDateTime.now());
  }
}
