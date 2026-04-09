package com.kipu.core.contacts.application.contact.create;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.application.contact.get.ContactSummaryResult;
import com.kipu.core.contacts.application.event.birthday.EnsureBirthdayEventUseCase;
import com.kipu.core.contacts.domain.exception.ContactFirstNameRequiredException;
import com.kipu.core.contacts.domain.model.Contact;
import com.kipu.core.contacts.domain.model.UserTag;
import com.kipu.core.contacts.domain.repository.ContactRepository;
import com.kipu.core.contacts.domain.repository.UserTagRepository;
import java.time.LocalDate;
import java.util.Map;
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
class CreateContactUseCaseTest {

  @Mock private ContactRepository contactRepository;
  @Mock private UserTagRepository tagRepository;
  @Mock private EnsureBirthdayEventUseCase ensureBirthdayEventUseCase;

  @InjectMocks private CreateContactUseCase createContactUseCase;

  private static final String BIRTHDAY_TAG_NAME = "Cumpleaños";

  @Test
  @DisplayName(
      "execute: Should create and save contact without birthday automation when birthdate is null")
  void execute_ShouldCreateSuccessfully_WhenBirthdateIsNull() {
    // Arrange
    CreateContactCommand command =
        new CreateContactCommand(
            UUID.randomUUID(), "Julian", "Miranda", "j@t.com", null, Map.of(), Set.of(), "UTC");

    // Act
    ContactSummaryResult result = createContactUseCase.execute(command);

    // Assert
    assertThat(result).isNotNull();
    verify(contactRepository).save(any(Contact.class));
    verify(tagRepository, never()).findByOwnerUserIdAndNameIgnoreCase(any(), anyString());
    // Corregido: Usamos eq(false) o simplemente false, pero asegurando que no haya matchers mixtos
    // problemáticos
    verify(ensureBirthdayEventUseCase, never()).execute(any(), any(), any(), anyBoolean());
  }

  @Test
  @DisplayName(
      "execute: Should automate birthday event when birthdate and Birthday tag are present")
  void execute_ShouldAutomateBirthday_WhenTagMatches() {
    // Arrange
    UUID ownerId = UUID.randomUUID();
    UUID tagId = UUID.randomUUID();
    String timezone = "America/Bogota";

    // El contacto DEBE tener el tagId para pasar el .filter() del UseCase
    CreateContactCommand command =
        new CreateContactCommand(
            ownerId,
            "Julian",
            "Miranda",
            "j@t.com",
            LocalDate.now(),
            Map.of(),
            Set.of(tagId),
            timezone);

    UserTag birthdayTag = UserTag.reconstitute(tagId, ownerId, BIRTHDAY_TAG_NAME, "#000");

    when(tagRepository.findByOwnerUserIdAndNameIgnoreCase(eq(ownerId), eq(BIRTHDAY_TAG_NAME)))
        .thenReturn(Optional.of(birthdayTag));

    // Act
    createContactUseCase.execute(command);

    // Assert
    // Corregido: Para evitar NPE en booleanos, usamos valores fijos o matchers de primitivos
    verify(ensureBirthdayEventUseCase)
        .execute(any(Contact.class), eq(tagId), eq(timezone), eq(false));
    verify(contactRepository).save(any(Contact.class));
  }

  @Test
  @DisplayName(
      "execute: Should not automate birthday if Birthday tag exists but is not assigned to the contact")
  void execute_ShouldNotAutomate_WhenTagExistsButNotAssigned() {
    // Arrange
    UUID ownerId = UUID.randomUUID();
    UUID otherTagId = UUID.randomUUID(); // Tag que tiene el contacto
    UUID birthdayTagId = UUID.randomUUID(); // Tag de la DB

    CreateContactCommand command =
        new CreateContactCommand(
            ownerId,
            "Julian",
            "Miranda",
            "j@t.com",
            LocalDate.now(),
            Map.of(),
            Set.of(otherTagId),
            "UTC");

    UserTag birthdayTag = UserTag.reconstitute(birthdayTagId, ownerId, BIRTHDAY_TAG_NAME, "#000");

    when(tagRepository.findByOwnerUserIdAndNameIgnoreCase(ownerId, BIRTHDAY_TAG_NAME))
        .thenReturn(Optional.of(birthdayTag));

    // Act
    createContactUseCase.execute(command);

    // Assert
    // No debe ejecutarse porque el filter(tag -> contact.getTagIds().contains(tag.getId())) falla
    verify(ensureBirthdayEventUseCase, never()).execute(any(), any(), any(), anyBoolean());
  }

  @Test
  @DisplayName("execute: Should not automate birthday if Birthday tag does not exist")
  void execute_ShouldNotAutomate_WhenTagNotFound() {
    // Arrange
    UUID ownerId = UUID.randomUUID();
    CreateContactCommand command =
        new CreateContactCommand(
            ownerId, "Julian", "Miranda", "j@t.com", LocalDate.now(), Map.of(), Set.of(), "UTC");

    when(tagRepository.findByOwnerUserIdAndNameIgnoreCase(any(), anyString()))
        .thenReturn(Optional.empty());

    // Act
    createContactUseCase.execute(command);

    // Assert
    verify(ensureBirthdayEventUseCase, never()).execute(any(), any(), any(), anyBoolean());
  }

  @Test
  @DisplayName("execute: Should throw exception and not save when firstName is blank")
  void execute_ShouldThrowException_WhenFirstNameIsInvalid() {
    // Arrange & Act & Assert
    CreateContactCommand command =
        new CreateContactCommand(
            UUID.randomUUID(), "  ", "Miranda", "j@t.com", null, Map.of(), Set.of(), null);

    assertThatThrownBy(() -> createContactUseCase.execute(command))
        .isInstanceOf(ContactFirstNameRequiredException.class);

    verify(contactRepository, never()).save(any());
  }

  @Test
  @DisplayName("execute: Mapeo completo de campos y persistencia")
  void execute_ShouldVerifyFullMapping() {
    // Arrange
    UUID ownerId = UUID.randomUUID();
    CreateContactCommand command =
        new CreateContactCommand(
            ownerId,
            "Julian",
            "Miranda",
            "j@t.com",
            LocalDate.of(1990, 1, 1),
            Map.of(),
            Set.of(),
            "UTC");

    // Act
    createContactUseCase.execute(command);

    // Assert
    ArgumentCaptor<Contact> captor = ArgumentCaptor.forClass(Contact.class);
    verify(contactRepository).save(captor.capture());

    Contact saved = captor.getValue();
    assertThat(saved.getFirstName()).isEqualTo("Julian");
    assertThat(saved.getOwnerUserId()).isEqualTo(ownerId);
  }

  @Test
  @DisplayName("validateCommand: Rama firstName == null (Branch Coverage)")
  void execute_ShouldThrowException_WhenFirstNameIsStrictlyNull() {
    // Arrange
    CreateContactCommand command =
        new CreateContactCommand(
            UUID.randomUUID(),
            null, // Rama NULL evaluada como TRUE
            "Miranda",
            "j@t.com",
            null,
            Map.of(),
            Set.of(),
            "UTC");

    // Act & Assert
    assertThatThrownBy(() -> createContactUseCase.execute(command))
        .isInstanceOf(ContactFirstNameRequiredException.class);

    // Verificamos que no se interactuó con el repositorio
    verify(contactRepository, never()).save(any(Contact.class));
  }
}
