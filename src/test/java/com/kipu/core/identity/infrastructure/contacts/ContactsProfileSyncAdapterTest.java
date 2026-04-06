package com.kipu.core.identity.infrastructure.contacts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.application.create.CreateContactCommand;
import com.kipu.core.contacts.application.create.CreateContactResult;
import com.kipu.core.contacts.application.create.CreateContactUseCase;
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
  @Mock private ContactRepository contactRepository;
  @Mock private Contact mockContact;

  @InjectMocks private ContactsProfileSyncAdapter contactsProfileSyncAdapter;

  @Test
  @DisplayName("createSelfContact: Should map parameters to command and return contact ID")
  void createSelfContact_ShouldMapToCommandAndReturnId() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID expectedContactId = UUID.randomUUID();
    String firstName = "Julian";
    String lastName = "Miranda";
    String email = "dev@kipu.com";
    LocalDate birthdate = LocalDate.of(1990, 1, 1);

    CreateContactResult mockResult = new CreateContactResult(expectedContactId);

    when(createContactUseCase.execute(any(CreateContactCommand.class))).thenReturn(mockResult);

    // Act
    ContactProfileInfo resultId =
        contactsProfileSyncAdapter.createSelfContact(userId, firstName, lastName, email, birthdate);

    // Assert
    assertNotNull(resultId);
    assertEquals(expectedContactId, resultId.contactId());

    // Verificación de la construcción del comando (Captor para asegurar integridad)
    ArgumentCaptor<CreateContactCommand> commandCaptor =
        ArgumentCaptor.forClass(CreateContactCommand.class);

    verify(createContactUseCase).execute(commandCaptor.capture());

    CreateContactCommand capturedCommand = commandCaptor.getValue();
    assertEquals(userId, capturedCommand.ownerUserId());
    assertEquals(firstName, capturedCommand.firstName());
    assertEquals(lastName, capturedCommand.lastName());
    assertEquals(email, capturedCommand.primaryEmail());
    assertEquals(birthdate, capturedCommand.birthdate());
    assertNotNull(capturedCommand.dynamicAttributes());
  }

  @Test
  @DisplayName("getContactById: Should return ContactProfileInfo when contact exists")
  void getContactById_WhenContactExists_ShouldReturnProfileInfo() {
    // Arrange
    UUID contactId = UUID.randomUUID();
    String firstName = "Julian";
    String lastName = "Miranda";

    // Configuramos el mock de la entidad de dominio Contact
    when(mockContact.getId()).thenReturn(contactId);
    when(mockContact.getFirstName()).thenReturn(firstName);
    when(mockContact.getLastName()).thenReturn(lastName);

    when(contactRepository.findById(contactId)).thenReturn(Optional.of(mockContact));

    // Act
    Optional<ContactProfileInfo> result = contactsProfileSyncAdapter.getContactById(contactId);

    // Assert
    verify(contactRepository).findById(contactId);
    assertNotNull(result);
    assertEquals(true, result.isPresent());
    assertEquals(contactId, result.get().contactId());
    assertEquals(firstName, result.get().firstName());
    assertEquals(lastName, result.get().lastName());
  }

  @Test
  @DisplayName("getContactById: Should return empty Optional when contact does not exist")
  void getContactById_WhenContactDoesNotExist_ShouldReturnEmpty() {
    // Arrange
    UUID contactId = UUID.randomUUID();
    when(contactRepository.findById(contactId)).thenReturn(Optional.empty());

    // Act
    Optional<ContactProfileInfo> result = contactsProfileSyncAdapter.getContactById(contactId);

    // Assert
    verify(contactRepository).findById(contactId);
    assertNotNull(result);
    assertEquals(false, result.isPresent());
  }
}
