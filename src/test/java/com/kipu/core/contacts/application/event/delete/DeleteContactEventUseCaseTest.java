package com.kipu.core.contacts.application.event.delete;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.domain.exception.ContactEventNotFoundException;
import com.kipu.core.contacts.domain.exception.ContactNotFoundException;
import com.kipu.core.contacts.domain.exception.UnauthorizedContactAccessException;
import com.kipu.core.contacts.domain.model.Contact;
import com.kipu.core.contacts.domain.model.ContactEvent;
import com.kipu.core.contacts.domain.model.enums.EventRecurrenceTypeEnum;
import com.kipu.core.contacts.domain.repository.ContactEventRepository;
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
class DeleteContactEventUseCaseTest {

  @Mock private ContactRepository contactRepository;
  @Mock private ContactEventRepository contactEventRepository;

  @InjectMocks private DeleteContactEventUseCase deleteContactEventUseCase;

  @Test
  @DisplayName(
      "execute: Should delete event when event exists and user owns the associated contact")
  void execute_ShouldDeleteEvent_WhenUserIsOwner() {
    // Arrange
    UUID authenticatedUserId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    UUID eventId = UUID.randomUUID();

    // Usamos el método de creación real del dominio con los parámetros actuales
    ContactEvent event =
        ContactEvent.create(
            contactId,
            "Title",
            "Desc",
            OffsetDateTime.now(),
            0,
            EventRecurrenceTypeEnum.ONCE,
            1,
            "UTC",
            Set.of());

    Contact contact =
        Contact.reconstitute(
            contactId, authenticatedUserId, "Juan", "Perez", null, null, Map.of(), Set.of(), null);

    when(contactEventRepository.findById(eventId)).thenReturn(Optional.of(event));
    when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));

    // Act
    deleteContactEventUseCase.execute(authenticatedUserId, eventId);

    // Assert
    verify(contactEventRepository).findById(eventId);
    verify(contactRepository).findById(contactId);
    verify(contactEventRepository).delete(eventId);
  }

  @Test
  @DisplayName("execute: Should throw ContactEventNotFoundException when event id does not exist")
  void execute_ShouldThrowContactEventNotFoundException_WhenEventDoesNotExist() {
    // Arrange
    UUID eventId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    when(contactEventRepository.findById(eventId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> deleteContactEventUseCase.execute(userId, eventId))
        .isInstanceOf(ContactEventNotFoundException.class);

    verify(contactEventRepository, never()).delete(any());
    verify(contactRepository, never()).findById(any());
  }

  @Test
  @DisplayName(
      "execute: Should throw ContactNotFoundException when the associated contact is missing")
  void execute_ShouldThrowContactNotFoundException_WhenAssociatedContactDoesNotExist() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    UUID eventId = UUID.randomUUID();

    ContactEvent event =
        ContactEvent.create(
            contactId,
            "Title",
            "Desc",
            OffsetDateTime.now(),
            0,
            EventRecurrenceTypeEnum.ONCE,
            1,
            "UTC",
            Set.of());

    when(contactEventRepository.findById(eventId)).thenReturn(Optional.of(event));
    when(contactRepository.findById(contactId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> deleteContactEventUseCase.execute(userId, eventId))
        .isInstanceOf(ContactNotFoundException.class);

    verify(contactEventRepository, never()).delete(any());
  }

  @Test
  @DisplayName(
      "execute: Should throw UnauthorizedContactAccessException when user is not the contact owner")
  void execute_ShouldThrowUnauthorizedContactAccessException_WhenUserIsNotOwner() {
    // Arrange
    UUID authenticatedUserId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    UUID eventId = UUID.randomUUID();
    UUID differentOwnerId = UUID.randomUUID();

    ContactEvent event =
        ContactEvent.create(
            contactId,
            "Title",
            "Desc",
            OffsetDateTime.now(),
            0,
            EventRecurrenceTypeEnum.ONCE,
            1,
            "UTC",
            Set.of());

    Contact contact =
        Contact.reconstitute(
            contactId, differentOwnerId, "Juan", "Perez", null, null, Map.of(), Set.of(), null);

    when(contactEventRepository.findById(eventId)).thenReturn(Optional.of(event));
    when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));

    // Act & Assert
    assertThatThrownBy(() -> deleteContactEventUseCase.execute(authenticatedUserId, eventId))
        .isInstanceOf(UnauthorizedContactAccessException.class);

    verify(contactEventRepository, never()).delete(any());
  }
}
