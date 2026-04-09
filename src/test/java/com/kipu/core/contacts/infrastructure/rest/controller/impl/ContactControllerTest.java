package com.kipu.core.contacts.infrastructure.rest.controller.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.application.contact.create.CreateContactCommand;
import com.kipu.core.contacts.application.contact.create.CreateContactUseCase;
import com.kipu.core.contacts.application.contact.delete.DeleteContactCommand;
import com.kipu.core.contacts.application.contact.delete.DeleteContactUseCase;
import com.kipu.core.contacts.application.contact.get.ContactDetailResult;
import com.kipu.core.contacts.application.contact.get.ContactSummaryResult;
import com.kipu.core.contacts.application.contact.get.GetContactDetailUseCase;
import com.kipu.core.contacts.application.contact.get.GetUserContactsUseCase;
import com.kipu.core.contacts.application.contact.update.UpdateContactCommand;
import com.kipu.core.contacts.application.contact.update.UpdateContactUseCase;
import com.kipu.core.contacts.infrastructure.rest.dto.ContactRequest;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ContactControllerTest {

  @Mock private CreateContactUseCase createContactUseCase;
  @Mock private UpdateContactUseCase updateContactUseCase;
  @Mock private DeleteContactUseCase deleteContactUseCase;
  @Mock private GetUserContactsUseCase getUserContactsUseCase;
  @Mock private GetContactDetailUseCase getContactDetailUseCase;

  @InjectMocks private ContactController contactController;

  @Test
  @DisplayName("createContact: Should return 201 Created and result when request is valid")
  void createContact_ReturnsCreated_WhenRequestIsValid() {
    // Arrange
    UUID userId = UUID.randomUUID();
    ContactRequest request =
        new ContactRequest(
            "Carol",
            "Gomez",
            "caro@test.com",
            LocalDate.now(),
            Map.of("key", "val"),
            Set.of(UUID.randomUUID()));
    ContactSummaryResult expected = createMockSummary();

    when(createContactUseCase.execute(any(CreateContactCommand.class))).thenReturn(expected);

    // Act
    ResponseEntity<ContactSummaryResult> response =
        contactController.createContact(userId, request);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isEqualTo(expected);
  }

  @Test
  @DisplayName(
      "createContact: Should handle null collections by providing empty Map and Set (Branch Coverage)")
  void createContact_HandlesNullCollections() {
    // Arrange
    UUID userId = UUID.randomUUID();
    ContactRequest request =
        new ContactRequest("Carol", "Gomez", "caro@test.com", null, null, null);
    when(createContactUseCase.execute(any(CreateContactCommand.class)))
        .thenReturn(createMockSummary());

    // Act
    contactController.createContact(userId, request);

    // Assert
    ArgumentCaptor<CreateContactCommand> captor =
        ArgumentCaptor.forClass(CreateContactCommand.class);
    verify(createContactUseCase).execute(captor.capture());

    // Verificamos que las ternarias funcionaron y enviaron colecciones vacías no nulas
    assertThat(captor.getValue().dynamicAttributes()).isNotNull().isEmpty();
    assertThat(captor.getValue().tagIds()).isNotNull().isEmpty();
  }

  @Test
  @DisplayName("updateContact: Should return 200 OK and handle null collections")
  void updateContact_HandlesNullCollections() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    ContactRequest request = new ContactRequest("Carol", "G", null, null, null, null);
    when(updateContactUseCase.execute(any(UpdateContactCommand.class)))
        .thenReturn(createMockSummary());

    // Act
    ResponseEntity<ContactSummaryResult> response =
        contactController.updateContact(userId, contactId, request);

    // Assert
    ArgumentCaptor<UpdateContactCommand> captor =
        ArgumentCaptor.forClass(UpdateContactCommand.class);
    verify(updateContactUseCase).execute(captor.capture());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(captor.getValue().dynamicAttributes()).isNotNull();
    assertThat(captor.getValue().tagIds()).isNotNull();
  }

  @Test
  @DisplayName("deleteContact: Should return 204 No Content")
  void deleteContact_ReturnsNoContent() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();

    // Act
    ResponseEntity<Void> response = contactController.deleteContact(userId, contactId);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    ArgumentCaptor<DeleteContactCommand> captor =
        ArgumentCaptor.forClass(DeleteContactCommand.class);
    verify(deleteContactUseCase).execute(captor.capture());
    assertThat(captor.getValue().contactId()).isEqualTo(contactId);
    assertThat(captor.getValue().authenticatedUserId()).isEqualTo(userId);
  }

  @Test
  @DisplayName("getContacts: Should return 200 OK with Page results")
  void getContacts_ReturnsOk() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID selfId = UUID.randomUUID();
    Pageable pageable = Pageable.unpaged();
    when(getUserContactsUseCase.execute(userId, selfId, pageable)).thenReturn(Page.empty());

    // Act
    ResponseEntity<Page<ContactSummaryResult>> response =
        contactController.getContacts(userId, selfId, pageable);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    verify(getUserContactsUseCase).execute(userId, selfId, pageable);
  }

  @Test
  @DisplayName("getContact: Should return 200 OK with Detail data")
  void getContact_ReturnsOk() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    ContactDetailResult expected =
        new ContactDetailResult(contactId, "C", "G", "c@t.com", null, null, Map.of(), Set.of());
    when(getContactDetailUseCase.execute(userId, contactId)).thenReturn(expected);

    // Act
    ResponseEntity<ContactDetailResult> response = contactController.getContact(userId, contactId);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(expected);
  }

  private ContactSummaryResult createMockSummary() {
    return new ContactSummaryResult(UUID.randomUUID(), "Carol", "Gomez", "caro@test.com", Set.of());
  }

  @Test
  @DisplayName("updateContact: Should return 200 OK and correctly map all fields to the Command")
  void updateContact_ReturnsOk_WhenRequestIsFull() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    Map<String, Object> attrs = Map.of("color", "azul");
    Set<UUID> tags = Set.of(UUID.randomUUID());

    ContactRequest request =
        new ContactRequest(
            "Julian", "Miranda", "juli@test.com", LocalDate.of(1995, 5, 20), attrs, tags);

    ContactSummaryResult expectedResult = createMockSummary();
    when(updateContactUseCase.execute(any(UpdateContactCommand.class))).thenReturn(expectedResult);

    // Act
    ResponseEntity<ContactSummaryResult> response =
        contactController.updateContact(userId, contactId, request);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(expectedResult);

    ArgumentCaptor<UpdateContactCommand> commandCaptor =
        ArgumentCaptor.forClass(UpdateContactCommand.class);
    verify(updateContactUseCase).execute(commandCaptor.capture());

    UpdateContactCommand capturedCommand = commandCaptor.getValue();
    assertThat(capturedCommand.authenticatedUserId()).isEqualTo(userId);
    assertThat(capturedCommand.contactId()).isEqualTo(contactId);
    assertThat(capturedCommand.dynamicAttributes()).isEqualTo(attrs);
    assertThat(capturedCommand.tagIds()).isEqualTo(tags);
  }

  @Test
  @DisplayName(
      "updateContact: Should provide empty Map and Set when dynamicAttributes and tagIds are null")
  void updateContact_HandlesNullCollections_ProvidingEmptyDefaults() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    // Enviamos null en las colecciones para forzar la ejecución de las ternarias
    ContactRequest request =
        new ContactRequest(
            "Julian", "Miranda", "juli@test.com", LocalDate.of(1995, 5, 20), null, null);

    when(updateContactUseCase.execute(any(UpdateContactCommand.class)))
        .thenReturn(createMockSummary());

    // Act
    contactController.updateContact(userId, contactId, request);

    // Assert
    ArgumentCaptor<UpdateContactCommand> commandCaptor =
        ArgumentCaptor.forClass(UpdateContactCommand.class);
    verify(updateContactUseCase).execute(commandCaptor.capture());

    UpdateContactCommand capturedCommand = commandCaptor.getValue();

    // Verificamos que NO sean null, sino colecciones vacías (Coverage del 100%)
    assertThat(capturedCommand.dynamicAttributes()).isNotNull().isEmpty();
    assertThat(capturedCommand.tagIds()).isNotNull().isEmpty();
  }
}
