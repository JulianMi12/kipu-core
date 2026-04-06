package com.kipu.core.contacts.application.create;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

import com.kipu.core.contacts.domain.model.Contact;
import com.kipu.core.contacts.domain.repository.ContactRepository;
import java.time.LocalDate;
import java.util.Map;
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
  @DisplayName("execute: Should create and save a new contact correctly")
  void execute_ShouldCreateAndSaveContact() {
    // Arrange
    UUID ownerUserId = UUID.randomUUID();
    String firstName = "Julian";
    String lastName = "Miranda";
    String email = "dev@kipu.com";
    LocalDate birthdate = LocalDate.of(1990, 1, 1);
    Map<String, Object> attributes = Map.of("origin", "onboarding");

    CreateContactCommand command =
        new CreateContactCommand(ownerUserId, firstName, lastName, email, birthdate, attributes);

    // Act
    CreateContactResult result = createContactUseCase.execute(command);

    // Assert
    assertNotNull(result);
    assertNotNull(result.contactId());

    // Verificación de la persistencia y captura del objeto de dominio
    ArgumentCaptor<Contact> contactCaptor = ArgumentCaptor.forClass(Contact.class);
    verify(contactRepository).save(contactCaptor.capture());

    Contact savedContact = contactCaptor.getValue();

    // Validamos que el ID del resultado coincida con el generado en el dominio
    assertEquals(savedContact.getId(), result.contactId());

    // Validamos la integridad de los datos mapeados desde el comando
    assertEquals(ownerUserId, savedContact.getOwnerUserId());
    assertEquals(firstName, savedContact.getFirstName());
    assertEquals(lastName, savedContact.getLastName());
    assertEquals(email, savedContact.getPrimaryEmail());
    assertEquals(birthdate, savedContact.getBirthdate());
    assertEquals(attributes, savedContact.getDynamicAttributes());
    assertNotNull(savedContact.getCreatedAt());
  }
}
