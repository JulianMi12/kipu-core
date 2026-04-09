package com.kipu.core.contacts.application.contact.create;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.kipu.core.contacts.application.contact.get.ContactSummaryResult;
import com.kipu.core.contacts.domain.exception.ContactFirstNameRequiredException;
import com.kipu.core.contacts.domain.model.Contact;
import com.kipu.core.contacts.domain.repository.ContactRepository;
import java.time.LocalDate;
import java.util.Map;
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

  @InjectMocks private CreateContactUseCase createContactUseCase;

  @Test
  @DisplayName("execute: Should create and save a new contact correctly when data is valid")
  void execute_ShouldCreateAndSaveContact() {
    // Arrange
    UUID ownerUserId = UUID.randomUUID();
    String firstName = "Julian";
    String lastName = "Miranda";
    String email = "dev@kipu.com";
    LocalDate birthdate = LocalDate.of(1990, 1, 1);
    Map<String, Object> attributes = Map.of("origin", "onboarding");
    Set<UUID> tagIds = Set.of(UUID.randomUUID());

    CreateContactCommand command =
        new CreateContactCommand(
            ownerUserId, firstName, lastName, email, birthdate, attributes, tagIds);

    // Act
    ContactSummaryResult result = createContactUseCase.execute(command);

    // Assert
    assertThat(result).isNotNull();

    ArgumentCaptor<Contact> contactCaptor = ArgumentCaptor.forClass(Contact.class);
    verify(contactRepository).save(contactCaptor.capture());

    Contact savedContact = contactCaptor.getValue();
    assertThat(savedContact.getId()).isEqualTo(result.id());
    assertThat(savedContact.getFirstName()).isEqualTo(firstName);
    assertThat(savedContact.getOwnerUserId()).isEqualTo(ownerUserId);
    assertThat(savedContact.getTagIds()).containsExactlyInAnyOrderElementsOf(tagIds);
    assertThat(savedContact.getCreatedAt()).isNotNull();
  }

  @Test
  @DisplayName("execute: Should throw exception when firstName is null")
  void execute_ShouldThrowException_WhenFirstNameIsNull() {
    // Arrange
    CreateContactCommand command =
        new CreateContactCommand(
            UUID.randomUUID(), null, "Miranda", "j@t.com", null, Map.of(), Set.of());

    // Act & Assert
    assertThatThrownBy(() -> createContactUseCase.execute(command))
        .isInstanceOf(ContactFirstNameRequiredException.class);

    verify(contactRepository, never()).save(any(Contact.class));
  }

  @Test
  @DisplayName("execute: Should throw exception when firstName is empty")
  void execute_ShouldThrowException_WhenFirstNameIsEmpty() {
    // Arrange
    CreateContactCommand command =
        new CreateContactCommand(
            UUID.randomUUID(), "", "Miranda", "j@t.com", null, Map.of(), Set.of());

    // Act & Assert
    assertThatThrownBy(() -> createContactUseCase.execute(command))
        .isInstanceOf(ContactFirstNameRequiredException.class);

    verify(contactRepository, never()).save(any(Contact.class));
  }
}
