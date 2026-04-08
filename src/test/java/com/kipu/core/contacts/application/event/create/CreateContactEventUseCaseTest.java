package com.kipu.core.contacts.application.event.create;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.domain.exception.ContactNotFoundException;
import com.kipu.core.contacts.domain.exception.UnauthorizedContactAccessException;
import com.kipu.core.contacts.domain.model.Contact;
import com.kipu.core.contacts.domain.model.ContactEvent;
import com.kipu.core.contacts.domain.model.enums.EventRecurrenceTypeEnum;
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
class CreateContactEventUseCaseTest {

  @Mock private ContactRepository contactRepository;
  @Mock private ContactEventRepository contactEventRepository;

  @InjectMocks private CreateContactEventUseCase createContactEventUseCase;

  @Test
  void execute_ShouldReturnEventResult_WhenDataIsValidAndUserIsOwner() {
    // Arrange
    UUID authenticatedUserId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    CreateContactEventCommand command =
        new CreateContactEventCommand(
            authenticatedUserId,
            contactId,
            "Renovar Seguro",
            "Seguro del auto",
            LocalDate.of(2026, 6, 15),
            15,
            EventRecurrenceTypeEnum.YEARLY,
            Set.of());

    // Reconstituimos un contacto donde el owner coincide con el del comando
    Contact contact =
        Contact.reconstitute(
            contactId, authenticatedUserId, "Juan", "Perez", null, null, null, Set.of(), null);

    when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));

    // Al guardar, devolvemos el mismo evento (simulando persistencia exitosa)
    when(contactEventRepository.save(any(ContactEvent.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    CreateContactEventResult result = createContactEventUseCase.execute(command);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.title()).isEqualTo("Renovar Seguro");
    assertThat(result.alertLeadTimeDays()).isEqualTo(15);
    assertThat(result.recurrenceType()).isEqualTo(EventRecurrenceTypeEnum.YEARLY);

    verify(contactRepository).findById(contactId);
    verify(contactEventRepository).save(any(ContactEvent.class));
  }

  @Test
  void execute_ShouldThrowContactNotFoundException_WhenContactDoesNotExist() {
    // Arrange
    UUID contactId = UUID.randomUUID();
    CreateContactEventCommand command =
        new CreateContactEventCommand(
            UUID.randomUUID(),
            contactId,
            "Title",
            "Desc",
            LocalDate.now(),
            0,
            EventRecurrenceTypeEnum.ONCE,
            Set.of());

    when(contactRepository.findById(contactId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> createContactEventUseCase.execute(command))
        .isInstanceOf(ContactNotFoundException.class);

    verify(contactEventRepository, never()).save(any());
  }

  @Test
  void execute_ShouldThrowUnauthorizedContactAccessException_WhenUserIsNotOwner() {
    // Arrange
    UUID authenticatedUserId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    UUID differentOwnerId = UUID.randomUUID();

    CreateContactEventCommand command =
        new CreateContactEventCommand(
            authenticatedUserId,
            contactId,
            "Title",
            "Desc",
            LocalDate.now(),
            0,
            EventRecurrenceTypeEnum.ONCE,
            Set.of());

    Contact contact =
        Contact.reconstitute(
            contactId, differentOwnerId, "Juan", "Perez", null, null, null, Set.of(), null);

    when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));

    // Act & Assert
    assertThatThrownBy(() -> createContactEventUseCase.execute(command))
        .isInstanceOf(UnauthorizedContactAccessException.class);

    verify(contactEventRepository, never()).save(any());
  }
}
