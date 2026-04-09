package com.kipu.core.contacts.application.event.birthday;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.application.event.create.CreateContactEventCommand;
import com.kipu.core.contacts.application.event.create.CreateContactEventUseCase;
import com.kipu.core.contacts.domain.model.Contact;
import com.kipu.core.contacts.domain.model.ContactEvent;
import com.kipu.core.contacts.domain.model.enums.EventRecurrenceTypeEnum;
import com.kipu.core.contacts.domain.repository.ContactEventRepository;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EnsureBirthdayEventUseCaseTest {

  @Mock private CreateContactEventUseCase createContactEventUseCase;
  @Mock private ContactEventRepository contactEventRepository;

  @InjectMocks private EnsureBirthdayEventUseCase ensureBirthdayEventUseCase;

  @Test
  @DisplayName("execute: Rama contact.getBirthdate() == null (Branch Coverage)")
  void execute_ShouldReturnImmediately_WhenBirthdateIsNull() {
    Contact contact = mock(Contact.class);
    when(contact.getBirthdate()).thenReturn(null);

    ensureBirthdayEventUseCase.execute(contact, UUID.randomUUID(), "UTC", false);

    verifyNoInteractions(contactEventRepository, createContactEventUseCase);
  }

  @Test
  @DisplayName("execute: Crear nuevo evento de cumpleaños para OTRO contacto (isSelf = false)")
  void execute_ShouldCreateNewEvent_ForOtherContact() {
    // Arrange
    UUID ownerId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    UUID tagId = UUID.randomUUID();
    // Ponemos el cumple mañana para que sea "este año"
    LocalDate birthdate = LocalDate.now().minusYears(20).plusDays(1);

    Contact contact =
        Contact.reconstitute(
            contactId, ownerId, "Julian", "M", "e", birthdate, null, Set.of(), null);

    when(contactEventRepository.findByContactIdAndTagIdsContains(contactId, tagId))
        .thenReturn(Optional.empty());

    // Act
    ensureBirthdayEventUseCase.execute(contact, tagId, "America/Bogota", false);

    // Assert
    ArgumentCaptor<CreateContactEventCommand> captor =
        ArgumentCaptor.forClass(CreateContactEventCommand.class);
    verify(createContactEventUseCase).execute(captor.capture());

    CreateContactEventCommand cmd = captor.getValue();
    assertThat(cmd.title()).contains("Cumpleaños de Julian");
    assertThat(cmd.recurrenceType()).isEqualTo(EventRecurrenceTypeEnum.YEARLY);
    assertThat(cmd.timezone()).isEqualTo("America/Bogota");
  }

  @Test
  @DisplayName("execute: Crear nuevo evento para MI MISMO (isSelf = true) y timezone NULL")
  void execute_ShouldCreateNewEvent_ForSelf_WithDefaultTz() {
    // Arrange
    UUID contactId = UUID.randomUUID();
    UUID tagId = UUID.randomUUID();
    // Cumpleaños ayer (para forzar cálculo de 'plusYears(1)')
    LocalDate birthdate = LocalDate.now().minusYears(30).minusDays(1);

    Contact contact =
        Contact.reconstitute(
            contactId, UUID.randomUUID(), "Yo", "M", "e", birthdate, null, Set.of(), null);

    when(contactEventRepository.findByContactIdAndTagIdsContains(contactId, tagId))
        .thenReturn(Optional.empty());

    // Act
    ensureBirthdayEventUseCase.execute(contact, tagId, null, true);

    // Assert
    ArgumentCaptor<CreateContactEventCommand> captor =
        ArgumentCaptor.forClass(CreateContactEventCommand.class);
    verify(createContactEventUseCase).execute(captor.capture());

    assertThat(captor.getValue().title()).isEqualTo("¡Feliz Cumpleaños! 🎂");
    assertThat(captor.getValue().timezone()).isEqualTo("UTC");
    // Verificar que el año sea el siguiente
    assertThat(captor.getValue().startDateTime().getYear())
        .isEqualTo(ZonedDateTime.now(ZoneId.of("UTC")).getYear() + 1);
  }

  @Test
  @DisplayName("execute: Actualizar evento existente")
  void execute_ShouldUpdateExistingEvent() {
    // Arrange
    UUID contactId = UUID.randomUUID();
    UUID tagId = UUID.randomUUID();
    LocalDate birthdate = LocalDate.of(1990, 5, 20);
    Contact contact =
        Contact.reconstitute(
            contactId, UUID.randomUUID(), "J", "M", "e", birthdate, null, Set.of(), null);

    ContactEvent existingEvent = mock(ContactEvent.class);
    when(existingEvent.getContactId()).thenReturn(contactId);

    when(contactEventRepository.findByContactIdAndTagIdsContains(contactId, tagId))
        .thenReturn(Optional.of(existingEvent));

    // Act
    ensureBirthdayEventUseCase.execute(contact, tagId, "UTC", false);

    // Assert
    verify(existingEvent).updateStartDateTime(any(OffsetDateTime.class));
    verify(contactEventRepository).save(existingEvent);
    verifyNoInteractions(createContactEventUseCase);
  }
}
