package com.kipu.core.contacts.application.event.update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.application.event.create.CreateContactEventResult;
import com.kipu.core.contacts.domain.exception.ContactEventNotFoundException;
import com.kipu.core.contacts.domain.exception.ContactNotFoundException;
import com.kipu.core.contacts.domain.exception.UnauthorizedContactAccessException;
import com.kipu.core.contacts.domain.model.Contact;
import com.kipu.core.contacts.domain.model.ContactEvent;
import com.kipu.core.contacts.domain.model.enums.EventRecurrenceTypeEnum;
import com.kipu.core.contacts.domain.repository.ContactEventRepository;
import com.kipu.core.contacts.domain.repository.ContactRepository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateContactEventUseCaseTest {

  @Mock private ContactRepository contactRepository;
  @Mock private ContactEventRepository contactEventRepository;

  @InjectMocks private UpdateContactEventUseCase updateContactEventUseCase;

  @Test
  void execute_ShouldReturnUpdatedResult_WhenUserIsOwner() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    UUID eventId = UUID.randomUUID();
    LocalDate newDate = LocalDate.now().plusDays(10);

    UpdateContactEventCommand command =
        new UpdateContactEventCommand(
            userId,
            eventId,
            "New Title",
            "New Description",
            newDate,
            5,
            EventRecurrenceTypeEnum.MONTHLY);

    ContactEvent existingEvent =
        ContactEvent.create(
            contactId, "Old Title", "Old Desc", LocalDate.now(), 0, EventRecurrenceTypeEnum.ONCE);

    Contact contact =
        Contact.reconstitute(contactId, userId, "Juan", "Perez", null, null, null, null);

    when(contactEventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));
    when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));
    when(contactEventRepository.save(any(ContactEvent.class))).thenAnswer(i -> i.getArgument(0));

    // Act
    CreateContactEventResult result = updateContactEventUseCase.execute(command);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.title()).isEqualTo("New Title");
    assertThat(result.description()).isEqualTo("New Description");
    assertThat(result.recurrenceType()).isEqualTo(EventRecurrenceTypeEnum.MONTHLY);

    verify(contactEventRepository).findById(eventId);
    verify(contactRepository).findById(contactId);
    verify(contactEventRepository).save(existingEvent);
  }

  @Test
  void execute_ShouldThrowContactEventNotFoundException_WhenEventDoesNotExist() {
    // Arrange
    UUID eventId = UUID.randomUUID();
    UpdateContactEventCommand command =
        new UpdateContactEventCommand(
            UUID.randomUUID(), eventId, "T", "D", LocalDate.now(), 0, EventRecurrenceTypeEnum.ONCE);

    when(contactEventRepository.findById(eventId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> updateContactEventUseCase.execute(command))
        .isInstanceOf(ContactEventNotFoundException.class);

    verify(contactEventRepository, never()).save(any());
  }

  @Test
  void execute_ShouldThrowContactNotFoundException_WhenContactDoesNotExist() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    UUID eventId = UUID.randomUUID();
    UpdateContactEventCommand command =
        new UpdateContactEventCommand(
            userId, eventId, "T", "D", LocalDate.now(), 0, EventRecurrenceTypeEnum.ONCE);

    ContactEvent event =
        ContactEvent.create(
            contactId, "Title", "Desc", LocalDate.now(), 0, EventRecurrenceTypeEnum.ONCE);

    when(contactEventRepository.findById(eventId)).thenReturn(Optional.of(event));
    when(contactRepository.findById(contactId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> updateContactEventUseCase.execute(command))
        .isInstanceOf(ContactNotFoundException.class);

    verify(contactEventRepository, never()).save(any());
  }

  @Test
  void execute_ShouldThrowUnauthorizedContactAccessException_WhenUserIsNotOwner() {
    // Arrange
    UUID authenticatedUserId = UUID.randomUUID();
    UUID differentOwnerId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    UUID eventId = UUID.randomUUID();

    UpdateContactEventCommand command =
        new UpdateContactEventCommand(
            authenticatedUserId,
            eventId,
            "T",
            "D",
            LocalDate.now(),
            0,
            EventRecurrenceTypeEnum.ONCE);

    ContactEvent event =
        ContactEvent.create(
            contactId, "Title", "Desc", LocalDate.now(), 0, EventRecurrenceTypeEnum.ONCE);

    Contact contact =
        Contact.reconstitute(contactId, differentOwnerId, "Juan", "Perez", null, null, null, null);

    when(contactEventRepository.findById(eventId)).thenReturn(Optional.of(event));
    when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));

    // Act & Assert
    assertThatThrownBy(() -> updateContactEventUseCase.execute(command))
        .isInstanceOf(UnauthorizedContactAccessException.class);

    verify(contactEventRepository, never()).save(any());
  }
}
