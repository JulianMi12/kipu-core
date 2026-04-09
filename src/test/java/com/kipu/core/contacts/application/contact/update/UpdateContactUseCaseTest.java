package com.kipu.core.contacts.application.contact.update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.application.contact.get.ContactSummaryResult;
import com.kipu.core.contacts.domain.exception.ContactNotFoundException;
import com.kipu.core.contacts.domain.exception.UnauthorizedContactAccessException;
import com.kipu.core.contacts.domain.model.Contact;
import com.kipu.core.contacts.domain.repository.ContactRepository;
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

  @InjectMocks private UpdateContactUseCase updateContactUseCase;

  @Test
  @DisplayName("execute: Should update and save contact when data and owner are valid")
  void execute_ShouldUpdateAndSave_WhenValid() {
    // Arrange
    UUID contactId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    Contact existingContact =
        Contact.reconstitute(
            contactId,
            userId,
            "OldName",
            "OldLast",
            "old@test.com",
            null,
            Map.of(),
            Set.of(),
            OffsetDateTime.now());

    UpdateContactCommand command =
        new UpdateContactCommand(
            userId,
            contactId,
            "NewName",
            "NewLast",
            "new@test.com",
            LocalDate.of(1990, 1, 1),
            Map.of("attr", "val"),
            Set.of(UUID.randomUUID()));

    when(contactRepository.findByIdWithTags(contactId)).thenReturn(Optional.of(existingContact));

    // Act
    ContactSummaryResult result = updateContactUseCase.execute(command);

    // Assert
    assertThat(result.firstName()).isEqualTo("NewName");
    verify(contactRepository).save(existingContact);
    assertThat(existingContact.getFirstName()).isEqualTo("NewName");
  }

  @Test
  @DisplayName("execute: Should throw ContactNotFoundException when contact does not exist")
  void execute_ShouldThrowNotFound_WhenIdDoesNotExist() {
    // Arrange
    UUID contactId = UUID.randomUUID();
    UpdateContactCommand command =
        new UpdateContactCommand(
            UUID.randomUUID(), contactId, "N", "L", "e@t.com", null, Map.of(), Set.of());

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

    Contact existingContact =
        Contact.reconstitute(
            contactId,
            realOwnerId,
            "Name",
            "Last",
            "e@t.com",
            null,
            Map.of(),
            Set.of(),
            OffsetDateTime.now());

    UpdateContactCommand command =
        new UpdateContactCommand(
            maliciousUserId, contactId, "Hack", "Hack", "h@t.com", null, Map.of(), Set.of());

    when(contactRepository.findByIdWithTags(contactId)).thenReturn(Optional.of(existingContact));

    // Act & Assert
    assertThatThrownBy(() -> updateContactUseCase.execute(command))
        .isInstanceOf(UnauthorizedContactAccessException.class);

    verify(contactRepository, never()).save(any());
  }
}
