package com.kipu.core.identity.infrastructure.contacts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.application.contact.create.CreateContactCommand;
import com.kipu.core.contacts.application.contact.create.CreateContactResult;
import com.kipu.core.contacts.application.contact.create.CreateContactUseCase;
import com.kipu.core.contacts.application.tag.create.CreateUserTagCommand;
import com.kipu.core.contacts.application.tag.create.CreateUserTagResult;
import com.kipu.core.contacts.application.tag.create.CreateUserTagUseCase;
import com.kipu.core.contacts.domain.model.Contact;
import com.kipu.core.contacts.domain.repository.ContactRepository;
import com.kipu.core.identity.domain.port.out.ContactProfileInfo;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContactsProfileSyncAdapterTest {

  @Mock private CreateContactUseCase createContactUseCase;
  @Mock private CreateUserTagUseCase createUserTagUseCase;
  @Mock private ContactRepository contactRepository;
  @Mock private Contact mockContact;

  @InjectMocks private ContactsProfileSyncAdapter contactsProfileSyncAdapter;

  @Test
  @DisplayName(
      "createSelfContact: Should create personal tag, then contact, and return profile info")
  void createSelfContact_ShouldCreateTagAndContactCorrectly() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID tagId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    String firstName = "Julian";
    String lastName = "Miranda";
    String email = "dev@kipu.com";
    LocalDate birthdate = LocalDate.of(1990, 1, 1);

    // Mocks para los casos de uso
    CreateUserTagResult tagResult = new CreateUserTagResult(tagId, "personal", "#4F46E5");
    CreateContactResult contactResult = new CreateContactResult(contactId);

    when(createUserTagUseCase.execute(any(CreateUserTagCommand.class))).thenReturn(tagResult);
    when(createContactUseCase.execute(any(CreateContactCommand.class))).thenReturn(contactResult);

    // Act
    ContactProfileInfo result =
        contactsProfileSyncAdapter.createSelfContact(userId, firstName, lastName, email, birthdate);

    // Assert
    assertNotNull(result);
    assertEquals(contactId, result.contactId());
    assertEquals(firstName, result.firstName());
    assertEquals(lastName, result.lastName());

    // 1. Verificar creación de Tag
    ArgumentCaptor<CreateUserTagCommand> tagCaptor =
        ArgumentCaptor.forClass(CreateUserTagCommand.class);
    verify(createUserTagUseCase).execute(tagCaptor.capture());
    assertEquals(userId, tagCaptor.getValue().ownerUserId());
    assertEquals("personal", tagCaptor.getValue().name());
    assertEquals("#4F46E5", tagCaptor.getValue().colorHex());

    // 2. Verificar creación de Contacto con el Tag ID recibido
    ArgumentCaptor<CreateContactCommand> contactCaptor =
        ArgumentCaptor.forClass(CreateContactCommand.class);
    verify(createContactUseCase).execute(contactCaptor.capture());

    CreateContactCommand capturedContact = contactCaptor.getValue();
    assertEquals(userId, capturedContact.ownerUserId());
    assertEquals(firstName, capturedContact.firstName());
    assertTrue(
        capturedContact.tagIds().contains(tagId), "El contacto debe incluir el ID del tag creado");
  }

  @Test
  @DisplayName("getContactById: Should return ContactProfileInfo when contact exists")
  void getContactById_WhenContactExists_ShouldReturnProfileInfo() {
    // Arrange
    UUID contactId = UUID.randomUUID();
    String firstName = "Julian";
    String lastName = "Miranda";

    when(mockContact.getId()).thenReturn(contactId);
    when(mockContact.getFirstName()).thenReturn(firstName);
    when(mockContact.getLastName()).thenReturn(lastName);
    when(contactRepository.findByIdWithTags(contactId)).thenReturn(Optional.of(mockContact));

    // Act
    Optional<ContactProfileInfo> result = contactsProfileSyncAdapter.getContactById(contactId);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(contactId, result.get().contactId());
    assertEquals(firstName, result.get().firstName());
    verify(contactRepository).findByIdWithTags(contactId);
  }

  @Test
  @DisplayName("getContactById: Should return empty Optional when contact does not exist")
  void getContactById_WhenContactDoesNotExist_ShouldReturnEmpty() {
    // Arrange
    UUID contactId = UUID.randomUUID();
    when(contactRepository.findByIdWithTags(contactId)).thenReturn(Optional.empty());

    // Act
    Optional<ContactProfileInfo> result = contactsProfileSyncAdapter.getContactById(contactId);

    // Assert
    assertTrue(result.isEmpty());
    verify(contactRepository).findByIdWithTags(contactId);
  }
}
