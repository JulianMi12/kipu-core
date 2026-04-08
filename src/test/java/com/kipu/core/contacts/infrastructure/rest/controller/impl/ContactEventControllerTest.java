package com.kipu.core.contacts.infrastructure.rest.controller.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.application.event.complete.CompleteContactEventCommand;
import com.kipu.core.contacts.application.event.complete.CompleteContactEventResult;
import com.kipu.core.contacts.application.event.complete.CompleteContactEventUseCase;
import com.kipu.core.contacts.application.event.create.CreateContactEventCommand;
import com.kipu.core.contacts.application.event.create.CreateContactEventResult;
import com.kipu.core.contacts.application.event.create.CreateContactEventUseCase;
import com.kipu.core.contacts.application.event.delete.DeleteContactEventUseCase;
import com.kipu.core.contacts.application.event.undo.UndoContactEventUseCase;
import com.kipu.core.contacts.application.event.update.UpdateContactEventCommand;
import com.kipu.core.contacts.application.event.update.UpdateContactEventUseCase;
import com.kipu.core.contacts.domain.model.enums.EventRecurrenceTypeEnum;
import com.kipu.core.contacts.domain.model.enums.EventStatusEnum;
import com.kipu.core.contacts.infrastructure.rest.dto.CreateContactEventRequest;
import com.kipu.core.contacts.infrastructure.rest.dto.UpdateContactEventRequest;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ContactEventControllerTest {

  @Mock private CreateContactEventUseCase createContactEventUseCase;
  @Mock private UpdateContactEventUseCase updateContactEventUseCase;
  @Mock private DeleteContactEventUseCase deleteContactEventUseCase;
  @Mock private CompleteContactEventUseCase completeContactEventUseCase;
  @Mock private UndoContactEventUseCase undoContactEventUseCase;

  @InjectMocks private ContactEventController contactEventController;

  @Test
  @DisplayName("createEvent: Should return 201 Created and the result when tags are provided")
  void createEvent_ReturnsCreatedAndResult_WhenRequestIsValid() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    LocalDate baseDate = LocalDate.now();
    Set<UUID> tagIds = Set.of(UUID.randomUUID());

    CreateContactEventRequest request =
        new CreateContactEventRequest(
            "Title", "Description", baseDate, 7, EventRecurrenceTypeEnum.ONCE, tagIds);

    CreateContactEventResult expectedResult = createMockResult(UUID.randomUUID(), tagIds);

    when(createContactEventUseCase.execute(any(CreateContactEventCommand.class)))
        .thenReturn(expectedResult);

    // Act
    ResponseEntity<CreateContactEventResult> response =
        contactEventController.createEvent(userId, contactId, request);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isEqualTo(expectedResult);

    ArgumentCaptor<CreateContactEventCommand> commandCaptor =
        ArgumentCaptor.forClass(CreateContactEventCommand.class);
    verify(createContactEventUseCase).execute(commandCaptor.capture());
    assertThat(commandCaptor.getValue().tagIds()).isEqualTo(tagIds);
  }

  @Test
  @DisplayName("createEvent: Should handle null tags by providing an empty set")
  void createEvent_ShouldHandleNullTags() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    CreateContactEventRequest request =
        new CreateContactEventRequest(
            "T", "D", LocalDate.now(), 0, EventRecurrenceTypeEnum.ONCE, null);

    when(createContactEventUseCase.execute(any(CreateContactEventCommand.class)))
        .thenReturn(createMockResult(UUID.randomUUID(), Set.of()));

    // Act
    contactEventController.createEvent(userId, contactId, request);

    // Assert
    ArgumentCaptor<CreateContactEventCommand> commandCaptor =
        ArgumentCaptor.forClass(CreateContactEventCommand.class);
    verify(createContactEventUseCase).execute(commandCaptor.capture());
    assertThat(commandCaptor.getValue().tagIds()).isEmpty();
  }

  @Test
  @DisplayName("updateEvent: Should return 200 OK and the result when request is valid")
  void updateEvent_ReturnsOkAndResult_WhenRequestIsValid() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    UUID eventId = UUID.randomUUID();
    LocalDate baseDate = LocalDate.now();

    UpdateContactEventRequest request =
        new UpdateContactEventRequest(
            "Updated Title",
            "Updated Desc",
            baseDate,
            10,
            EventRecurrenceTypeEnum.MONTHLY,
            Set.of());

    CreateContactEventResult expectedResult = createMockResult(eventId, Set.of());

    when(updateContactEventUseCase.execute(any(UpdateContactEventCommand.class)))
        .thenReturn(expectedResult);

    // Act
    ResponseEntity<CreateContactEventResult> response =
        contactEventController.updateEvent(userId, contactId, eventId, request);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(expectedResult);
    verify(updateContactEventUseCase).execute(any(UpdateContactEventCommand.class));
  }

  @Test
  @DisplayName("deleteEvent: Should return 204 No Content")
  void deleteEvent_ReturnsNoContent_WhenCalledWithValidIds() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    UUID eventId = UUID.randomUUID();

    // Act
    ResponseEntity<Void> response = contactEventController.deleteEvent(userId, contactId, eventId);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    verify(deleteContactEventUseCase).execute(userId, eventId);
  }

  @Test
  @DisplayName("completeEvent: Should return 200 OK and status COMPLETED")
  void completeEvent_ReturnsOkAndResult_WhenCalledWithValidIds() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    UUID eventId = UUID.randomUUID();

    CompleteContactEventResult expectedResult =
        new CompleteContactEventResult(
            eventId,
            "Title",
            "Desc",
            LocalDate.now(),
            7,
            EventRecurrenceTypeEnum.ONCE,
            EventStatusEnum.COMPLETED,
            LocalDate.now(),
            OffsetDateTime.now(),
            OffsetDateTime.now());

    when(completeContactEventUseCase.execute(any(CompleteContactEventCommand.class)))
        .thenReturn(expectedResult);

    // Act
    ResponseEntity<CompleteContactEventResult> response =
        contactEventController.completeEvent(userId, contactId, eventId);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().status()).isEqualTo(EventStatusEnum.COMPLETED);
    verify(completeContactEventUseCase).execute(any(CompleteContactEventCommand.class));
  }

  @Test
  @DisplayName("undoEvent: Should return 200 OK and status PENDING")
  void undoEvent_ReturnsOkAndResult_WhenCalledWithValidIds() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    UUID eventId = UUID.randomUUID();

    CompleteContactEventResult expectedResult =
        new CompleteContactEventResult(
            eventId,
            "Title",
            "Desc",
            LocalDate.now(),
            7,
            EventRecurrenceTypeEnum.ONCE,
            EventStatusEnum.PENDING,
            null,
            OffsetDateTime.now(),
            OffsetDateTime.now());

    when(undoContactEventUseCase.execute(userId, eventId)).thenReturn(expectedResult);

    // Act
    ResponseEntity<CompleteContactEventResult> response =
        contactEventController.undoEvent(userId, contactId, eventId);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().status()).isEqualTo(EventStatusEnum.PENDING);
    verify(undoContactEventUseCase).execute(userId, eventId);
  }

  private CreateContactEventResult createMockResult(UUID id, Set<UUID> tags) {
    return new CreateContactEventResult(
        id,
        "Title",
        "Desc",
        LocalDate.now(),
        0,
        EventRecurrenceTypeEnum.ONCE,
        EventStatusEnum.PENDING,
        null,
        tags,
        OffsetDateTime.now(),
        OffsetDateTime.now());
  }

  @Test
  @DisplayName("updateEvent: Should handle null tags in request by providing an empty set")
  void updateEvent_ShouldHandleNullTags() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    UUID eventId = UUID.randomUUID();

    // Request con tagIds nulo
    UpdateContactEventRequest request =
        new UpdateContactEventRequest(
            "Updated Title",
            "Updated Desc",
            LocalDate.now(),
            10,
            EventRecurrenceTypeEnum.MONTHLY,
            null);

    CreateContactEventResult expectedResult = createMockResult(eventId, Set.of());

    when(updateContactEventUseCase.execute(any(UpdateContactEventCommand.class)))
        .thenReturn(expectedResult);

    // Act
    contactEventController.updateEvent(userId, contactId, eventId, request);

    // Assert
    ArgumentCaptor<UpdateContactEventCommand> commandCaptor =
        ArgumentCaptor.forClass(UpdateContactEventCommand.class);

    verify(updateContactEventUseCase).execute(commandCaptor.capture());

    // Verificamos que se haya pasado un Set vacío y no null al Use Case
    assertThat(commandCaptor.getValue().tagIds()).isNotNull();
    assertThat(commandCaptor.getValue().tagIds()).isEmpty();
  }
}
