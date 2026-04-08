package com.kipu.core.contacts.application.event.undo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.application.event.complete.CompleteContactEventResult;
import com.kipu.core.contacts.domain.exception.ContactEventNotFoundException;
import com.kipu.core.contacts.domain.exception.ContactNotFoundException;
import com.kipu.core.contacts.domain.exception.UnauthorizedContactAccessException;
import com.kipu.core.contacts.domain.model.Contact;
import com.kipu.core.contacts.domain.model.ContactEvent;
import com.kipu.core.contacts.domain.model.enums.EventRecurrenceTypeEnum;
import com.kipu.core.contacts.domain.model.enums.EventStatusEnum;
import com.kipu.core.contacts.domain.repository.ContactEventRepository;
import com.kipu.core.contacts.domain.repository.ContactRepository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UndoContactEventUseCaseTest {

  @Mock private ContactRepository contactRepository;
  @Mock private ContactEventRepository contactEventRepository;

  @InjectMocks private UndoContactEventUseCase undoContactEventUseCase;

  @Test
  void execute_ShouldReturnResult_WhenUndoIsSuccessful() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    UUID eventId = UUID.randomUUID();
    LocalDate originalDate = LocalDate.now().minusDays(1);

    ContactEvent event =
        ContactEvent.create(
            contactId,
            "Gym",
            "Description",
            originalDate,
            0,
            EventRecurrenceTypeEnum.ONCE,
            Set.of());
    event.complete(); // Estado actual: COMPLETED, lastCompletedDate: today

    Contact contact =
        Contact.reconstitute(contactId, userId, "Juan", "Perez", null, null, null, Set.of(), null);

    when(contactEventRepository.findById(eventId)).thenReturn(Optional.of(event));
    when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));
    when(contactEventRepository.save(any(ContactEvent.class))).thenAnswer(i -> i.getArgument(0));

    // Act
    CompleteContactEventResult result = undoContactEventUseCase.execute(userId, eventId);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(EventStatusEnum.PENDING);
    assertThat(result.lastCompletedDate()).isNull();

    verify(contactEventRepository).findById(eventId);
    verify(contactRepository).findById(contactId);
    verify(contactEventRepository).save(event);
  }

  @Test
  void execute_ShouldThrowContactEventNotFoundException_WhenEventDoesNotExist() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID eventId = UUID.randomUUID();

    when(contactEventRepository.findById(eventId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> undoContactEventUseCase.execute(userId, eventId))
        .isInstanceOf(ContactEventNotFoundException.class);

    verify(contactEventRepository, never()).save(any());
  }

  @Test
  void execute_ShouldThrowContactNotFoundException_WhenContactDoesNotExist() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    UUID eventId = UUID.randomUUID();

    ContactEvent event =
        ContactEvent.create(
            contactId, "Title", "Desc", LocalDate.now(), 0, EventRecurrenceTypeEnum.ONCE, Set.of());

    when(contactEventRepository.findById(eventId)).thenReturn(Optional.of(event));
    when(contactRepository.findById(contactId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> undoContactEventUseCase.execute(userId, eventId))
        .isInstanceOf(ContactNotFoundException.class);

    verify(contactEventRepository, never()).save(any());
  }

  @Test
  void execute_ShouldThrowUnauthorizedContactAccessException_WhenUserIsNotOwner() {
    // Arrange
    UUID authenticatedUserId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    UUID differentOwnerId = UUID.randomUUID();
    UUID eventId = UUID.randomUUID();

    ContactEvent event =
        ContactEvent.create(
            contactId, "Title", "Desc", LocalDate.now(), 0, EventRecurrenceTypeEnum.ONCE, Set.of());

    Contact contact =
        Contact.reconstitute(
            contactId, differentOwnerId, "Juan", "Perez", null, null, null, Set.of(), null);

    when(contactEventRepository.findById(eventId)).thenReturn(Optional.of(event));
    when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));

    // Act & Assert
    assertThatThrownBy(() -> undoContactEventUseCase.execute(authenticatedUserId, eventId))
        .isInstanceOf(UnauthorizedContactAccessException.class);

    verify(contactEventRepository, never()).save(any());
  }
}
