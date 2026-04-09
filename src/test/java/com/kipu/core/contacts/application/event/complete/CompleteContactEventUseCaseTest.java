package com.kipu.core.contacts.application.event.complete;

import static org.assertj.core.api.Assertions.assertThat;
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
import com.kipu.core.contacts.domain.model.enums.EventStatusEnum;
import com.kipu.core.contacts.domain.repository.ContactEventRepository;
import com.kipu.core.contacts.domain.repository.ContactRepository;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompleteContactEventUseCaseTest {

  @Mock private ContactRepository contactRepository;
  @Mock private ContactEventRepository contactEventRepository;

  @InjectMocks private CompleteContactEventUseCase completeContactEventUseCase;

  @Test
  void execute_ShouldReturnResult_WhenCompletionIsSuccessful() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    UUID eventId = UUID.randomUUID();
    CompleteContactEventCommand command = new CompleteContactEventCommand(userId, eventId);

    // 1. Corregido: Firma de 9 argumentos y tipos correctos
    ContactEvent event =
        ContactEvent.create(
            contactId,
            "Title",
            "Desc",
            OffsetDateTime.now(),
            0,
            EventRecurrenceTypeEnum.ONCE,
            1, // recurrenceInterval
            "UTC", // timezone
            Set.of());

    // 2. Corregido: Reconstitute del contacto con Map.of() para evitar NPE en atributos
    Contact contact =
        Contact.reconstitute(
            contactId,
            userId,
            "Juan",
            "Perez",
            "juan@test.com",
            null,
            java.util.Map.of(),
            Set.of(),
            null);

    when(contactEventRepository.findById(eventId)).thenReturn(Optional.of(event));
    when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));
    when(contactEventRepository.save(any(ContactEvent.class))).thenAnswer(i -> i.getArgument(0));

    // Act
    CompleteContactEventResult result = completeContactEventUseCase.execute(command);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(EventStatusEnum.COMPLETED);

    // 3. Corregido: Comparación de fechas usando el mismo tipo (OffsetDateTime)
    // Usamos isBeforeOrEqualTo porque el dominio usa OffsetDateTime.now()
    assertThat(result.lastCompletedDate()).isNotNull();
    assertThat(result.lastCompletedDate().toLocalDate()).isEqualTo(LocalDate.now());

    verify(contactEventRepository).findById(eventId);
    verify(contactRepository).findById(contactId);
    verify(contactEventRepository).save(event);
  }

  @Test
  void execute_ShouldThrowContactEventNotFoundException_WhenEventDoesNotExist() {
    // Arrange
    UUID eventId = UUID.randomUUID();
    CompleteContactEventCommand command =
        new CompleteContactEventCommand(UUID.randomUUID(), eventId);

    when(contactEventRepository.findById(eventId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> completeContactEventUseCase.execute(command))
        .isInstanceOf(ContactEventNotFoundException.class);

    verify(contactEventRepository, never()).save(any());
  }

  @Test
  void execute_ShouldThrowContactNotFoundException_WhenContactDoesNotExist() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    UUID eventId = UUID.randomUUID();
    CompleteContactEventCommand command = new CompleteContactEventCommand(userId, eventId);

    ContactEvent event =
        ContactEvent.create(
            contactId,
            "Title",
            "Desc",
            OffsetDateTime.now(),
            0,
            EventRecurrenceTypeEnum.ONCE,
            0,
            null,
            Set.of());

    when(contactEventRepository.findById(eventId)).thenReturn(Optional.of(event));
    when(contactRepository.findById(contactId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> completeContactEventUseCase.execute(command))
        .isInstanceOf(ContactNotFoundException.class);

    verify(contactEventRepository, never()).save(any());
  }

  @Test
  void execute_ShouldThrowUnauthorizedContactAccessException_WhenUserIsNotOwner() {
    // Arrange
    UUID authenticatedUserId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    UUID eventId = UUID.randomUUID();
    UUID differentOwnerId = UUID.randomUUID();
    CompleteContactEventCommand command =
        new CompleteContactEventCommand(authenticatedUserId, eventId);

    ContactEvent event =
        ContactEvent.create(
            contactId,
            "Title",
            "Desc",
            OffsetDateTime.now(),
            0,
            EventRecurrenceTypeEnum.ONCE,
            0,
            null,
            Set.of());

    Contact contact =
        Contact.reconstitute(
            contactId, differentOwnerId, "Juan", "Perez", null, null, null, Set.of(), null);

    when(contactEventRepository.findById(eventId)).thenReturn(Optional.of(event));
    when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));

    // Act & Assert
    assertThatThrownBy(() -> completeContactEventUseCase.execute(command))
        .isInstanceOf(UnauthorizedContactAccessException.class);

    verify(contactEventRepository, never()).save(any());
  }
}
