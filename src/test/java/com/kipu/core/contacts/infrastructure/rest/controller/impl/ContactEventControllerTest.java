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

  private static final String DEFAULT_TZ = "UTC";

  @Test
  @DisplayName("createEvent: Should return 201 Created and map all fields including timezone")
  void createEvent_ReturnsCreatedAndResult_WhenRequestIsValid() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    OffsetDateTime startDate = OffsetDateTime.now();
    Set<UUID> tagIds = Set.of(UUID.randomUUID());

    CreateContactEventRequest request =
        new CreateContactEventRequest(
            "Title",
            "Description",
            startDate,
            7,
            EventRecurrenceTypeEnum.ONCE,
            1,
            DEFAULT_TZ,
            tagIds);

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
    assertThat(commandCaptor.getValue().timezone()).isEqualTo(DEFAULT_TZ);
    assertThat(commandCaptor.getValue().tagIds()).isEqualTo(tagIds);
  }

  @Test
  @DisplayName("updateEvent: Should return 200 OK and update recurrence interval")
  void updateEvent_ReturnsOkAndResult_WhenRequestIsValid() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    UUID eventId = UUID.randomUUID();
    OffsetDateTime newDate = OffsetDateTime.now().plusDays(1);

    UpdateContactEventRequest request =
        new UpdateContactEventRequest(
            "Updated",
            "Desc",
            newDate,
            10,
            EventRecurrenceTypeEnum.MONTHLY,
            2,
            "Europe/Madrid",
            Set.of());

    CreateContactEventResult expectedResult = createMockResult(eventId, Set.of());

    when(updateContactEventUseCase.execute(any(UpdateContactEventCommand.class)))
        .thenReturn(expectedResult);

    // Act
    ResponseEntity<CreateContactEventResult> response =
        contactEventController.updateEvent(userId, contactId, eventId, request);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ArgumentCaptor<UpdateContactEventCommand> captor =
        ArgumentCaptor.forClass(UpdateContactEventCommand.class);
    verify(updateContactEventUseCase).execute(captor.capture());
    assertThat(captor.getValue().recurrenceInterval()).isEqualTo(2);
  }

  @Test
  @DisplayName("completeEvent: Should handle OffsetDateTime correctly in results")
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
            OffsetDateTime.now(),
            7,
            EventRecurrenceTypeEnum.ONCE,
            EventStatusEnum.COMPLETED,
            OffsetDateTime.now(),
            DEFAULT_TZ,
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
  }

  @Test
  @DisplayName("undoEvent: Should return status PENDING and null lastCompletedDate")
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
            OffsetDateTime.now(),
            7,
            EventRecurrenceTypeEnum.ONCE,
            EventStatusEnum.PENDING,
            null,
            DEFAULT_TZ,
            OffsetDateTime.now(),
            OffsetDateTime.now());

    when(undoContactEventUseCase.execute(userId, eventId)).thenReturn(expectedResult);

    // Act
    ResponseEntity<CompleteContactEventResult> response =
        contactEventController.undoEvent(userId, contactId, eventId);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().lastCompletedDate()).isNull();
  }

  @Test
  @DisplayName("deleteEvent: Should return 204 No Content")
  void deleteEvent_ReturnsNoContent_WhenCalledWithValidIds() {
    UUID userId = UUID.randomUUID();
    UUID eventId = UUID.randomUUID();

    ResponseEntity<Void> response =
        contactEventController.deleteEvent(userId, UUID.randomUUID(), eventId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    verify(deleteContactEventUseCase).execute(userId, eventId);
  }

  private CreateContactEventResult createMockResult(UUID id, Set<UUID> tags) {
    return new CreateContactEventResult(
        id,
        "Title",
        "Desc",
        OffsetDateTime.now(),
        0,
        EventRecurrenceTypeEnum.ONCE,
        EventStatusEnum.PENDING,
        null,
        DEFAULT_TZ,
        tags);
  }

  @Test
  @DisplayName(
      "createEvent: Should provide empty set when request tagIds is null (Branch Coverage)")
  void createEvent_ShouldProvideEmptySet_WhenTagIdsIsNull() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    // Request con tagIds nulo
    CreateContactEventRequest request =
        new CreateContactEventRequest(
            "Title",
            "Desc",
            OffsetDateTime.now(),
            0,
            EventRecurrenceTypeEnum.ONCE,
            1,
            DEFAULT_TZ,
            null);

    when(createContactEventUseCase.execute(any(CreateContactEventCommand.class)))
        .thenReturn(createMockResult(UUID.randomUUID(), Set.of()));

    // Act
    contactEventController.createEvent(userId, contactId, request);

    // Assert
    ArgumentCaptor<CreateContactEventCommand> commandCaptor =
        ArgumentCaptor.forClass(CreateContactEventCommand.class);
    verify(createContactEventUseCase).execute(commandCaptor.capture());

    // Verificamos que se ejecutó la rama 'false' del ternario y pasó un Set vacío
    assertThat(commandCaptor.getValue().tagIds()).isNotNull().isEmpty();
  }

  @Test
  @DisplayName(
      "updateEvent: Should provide empty set when request tagIds is null (Branch Coverage)")
  void updateEvent_ShouldProvideEmptySet_WhenTagIdsIsNull() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    UUID eventId = UUID.randomUUID();
    // Request con tagIds nulo
    UpdateContactEventRequest request =
        new UpdateContactEventRequest(
            "Title",
            "Desc",
            OffsetDateTime.now(),
            0,
            EventRecurrenceTypeEnum.ONCE,
            1,
            DEFAULT_TZ,
            null);

    when(updateContactEventUseCase.execute(any(UpdateContactEventCommand.class)))
        .thenReturn(createMockResult(eventId, Set.of()));

    // Act
    contactEventController.updateEvent(userId, contactId, eventId, request);

    // Assert
    ArgumentCaptor<UpdateContactEventCommand> commandCaptor =
        ArgumentCaptor.forClass(UpdateContactEventCommand.class);
    verify(updateContactEventUseCase).execute(commandCaptor.capture());

    // Verificamos que se asignó Set.of() en lugar de null
    assertThat(commandCaptor.getValue().tagIds()).isNotNull().isEmpty();
  }
}
