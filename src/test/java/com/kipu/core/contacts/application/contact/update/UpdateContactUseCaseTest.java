package com.kipu.core.contacts.application.contact.update;

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
import com.kipu.core.contacts.domain.exception.ContactNotFoundException;
import com.kipu.core.contacts.domain.exception.UnauthorizedContactAccessException;
import com.kipu.core.contacts.domain.model.Contact;
import com.kipu.core.contacts.domain.model.UserTag;
import com.kipu.core.contacts.domain.repository.ContactRepository;
import com.kipu.core.contacts.domain.repository.UserTagRepository;
import java.time.LocalDate;
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
class UpdateContactUseCaseTest {

  @Mock private ContactRepository contactRepository;
  @Mock private UserTagRepository tagRepository;
  @Mock private EnsureBirthdayEventUseCase ensureBirthdayEventUseCase;

  @InjectMocks private UpdateContactUseCase updateContactUseCase;

  @Test
  @DisplayName(
      "execute: Should update, save and trigger birthday automation when birthdate is present and tag exists")
  void execute_ShouldUpdateAndTriggerBirthday_WhenValid() {
    // Arrange
    UUID contactId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    UUID birthdayTagId = UUID.randomUUID();
    LocalDate birthdate = LocalDate.of(1990, 1, 1);
    String timezone = "America/Bogota";

    Contact existingContact = createDefaultContact(contactId, userId);
    UserTag birthdayTag = UserTag.reconstitute(birthdayTagId, userId, "Cumpleaños", "#FFFFFF");

    UpdateContactCommand command =
        new UpdateContactCommand(
            userId,
            contactId,
            "NewName",
            "NewLast",
            "new@test.com",
            birthdate,
            Map.of(),
            Set.of(birthdayTagId),
            timezone);

    when(contactRepository.findByIdWithTags(contactId)).thenReturn(Optional.of(existingContact));
    when(tagRepository.findByOwnerUserIdAndNameIgnoreCase(userId, "Cumpleaños"))
        .thenReturn(Optional.of(birthdayTag));

    // Act
    ContactSummaryResult result = updateContactUseCase.execute(command);

    // Assert
    assertThat(result.firstName()).isEqualTo("NewName");
    verify(contactRepository).save(existingContact);
    verify(ensureBirthdayEventUseCase)
        .execute(eq(existingContact), eq(birthdayTagId), eq(timezone), eq(false));
  }

  @Test
  @DisplayName("execute: Should update but NOT trigger birthday automation when birthdate is null")
  void execute_ShouldUpdateButNotTriggerBirthday_WhenBirthdateIsNull() {
    // Arrange
    UUID contactId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    Contact existingContact = createDefaultContact(contactId, userId);

    UpdateContactCommand command =
        new UpdateContactCommand(
            userId,
            contactId,
            "NewName",
            "NewLast",
            "new@test.com",
            null,
            Map.of(),
            Set.of(),
            "UTC");

    when(contactRepository.findByIdWithTags(contactId)).thenReturn(Optional.of(existingContact));

    // Act
    updateContactUseCase.execute(command);

    // Assert
    verify(contactRepository).save(existingContact);
    verify(ensureBirthdayEventUseCase, never()).execute(any(), any(), anyString(), anyBoolean());
  }

  @Test
  @DisplayName("execute: Should throw ContactNotFoundException when contact does not exist")
  void execute_ShouldThrowNotFound_WhenIdDoesNotExist() {
    // Arrange
    UUID contactId = UUID.randomUUID();
    UpdateContactCommand command =
        new UpdateContactCommand(
            UUID.randomUUID(), contactId, "N", "L", "e@t.com", null, Map.of(), Set.of(), "UTC");

    when(contactRepository.findByIdWithTags(contactId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> updateContactUseCase.execute(command))
        .isInstanceOf(ContactNotFoundException.class);

    verify(contactRepository, never()).save(any());
  }

  @Test
  @DisplayName(
      "execute: Should throw UnauthorizedContactAccessException when user is not the owner")
  void execute_ShouldThrowUnauthorized_WhenUserIsNotOwner() {
    // Arrange
    UUID contactId = UUID.randomUUID();
    UUID realOwnerId = UUID.randomUUID();
    UUID maliciousUserId = UUID.randomUUID();

    Contact existingContact = createDefaultContact(contactId, realOwnerId);
    UpdateContactCommand command =
        new UpdateContactCommand(
            maliciousUserId, contactId, "Hack", "Hack", "h@t.com", null, Map.of(), Set.of(), "UTC");

    when(contactRepository.findByIdWithTags(contactId)).thenReturn(Optional.of(existingContact));

    // Act & Assert
    assertThatThrownBy(() -> updateContactUseCase.execute(command))
        .isInstanceOf(UnauthorizedContactAccessException.class);

    verify(contactRepository, never()).save(any());
  }

  private Contact createDefaultContact(UUID contactId, UUID ownerId) {
    return Contact.reconstitute(
        contactId,
        ownerId,
        "OldName",
        "OldLast",
        "old@test.com",
        null,
        Map.of(),
        Set.of(),
        OffsetDateTime.now());
  }
}
