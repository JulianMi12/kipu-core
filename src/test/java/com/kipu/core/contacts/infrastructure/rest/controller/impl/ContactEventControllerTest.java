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
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
  void createEvent_ReturnsCreatedAndResult_WhenRequestIsValid() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    LocalDate baseDate = LocalDate.now();

    CreateContactEventRequest request =
        new CreateContactEventRequest(
            "Title", "Description", baseDate, 7, EventRecurrenceTypeEnum.ONCE);

    CreateContactEventResult expectedResult =
        new CreateContactEventResult(
            UUID.randomUUID(),
            "Title",
            "Description",
            baseDate,
            7,
            EventRecurrenceTypeEnum.ONCE,
            EventStatusEnum.PENDING,
            null,
            OffsetDateTime.now(),
            OffsetDateTime.now());

    when(createContactEventUseCase.execute(any(CreateContactEventCommand.class)))
        .thenReturn(expectedResult);

    // Act
    ResponseEntity<CreateContactEventResult> response =
        contactEventController.createEvent(userId, contactId, request);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isEqualTo(expectedResult);
    verify(createContactEventUseCase).execute(any(CreateContactEventCommand.class));
  }

  @Test
  void updateEvent_ReturnsOkAndResult_WhenRequestIsValid() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    UUID eventId = UUID.randomUUID();
    LocalDate baseDate = LocalDate.now();

    UpdateContactEventRequest request =
        new UpdateContactEventRequest(
            "Updated Title", "Updated Desc", baseDate, 10, EventRecurrenceTypeEnum.MONTHLY);

    CreateContactEventResult expectedResult =
        new CreateContactEventResult(
            eventId,
            "Updated Title",
            "Updated Desc",
            baseDate,
            10,
            EventRecurrenceTypeEnum.MONTHLY,
            EventStatusEnum.PENDING,
            null,
            OffsetDateTime.now(),
            OffsetDateTime.now());

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
  void deleteEvent_ReturnsNoContent_WhenCalledWithValidIds() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    UUID eventId = UUID.randomUUID();

    // Act
    ResponseEntity<Void> response = contactEventController.deleteEvent(userId, contactId, eventId);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(response.getBody()).isNull();
    verify(deleteContactEventUseCase).execute(userId, eventId);
  }

  @Test
  void completeEvent_ReturnsOkAndResult_WhenCalledWithValidIds() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    UUID eventId = UUID.randomUUID();

    // Asumiendo que CompleteContactEventResult tiene una estructura similar
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
    assertThat(response.getBody()).isEqualTo(expectedResult);
    verify(completeContactEventUseCase).execute(any(CompleteContactEventCommand.class));
  }

  @Test
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
    assertThat(response.getBody()).isEqualTo(expectedResult);
    verify(undoContactEventUseCase).execute(userId, eventId);
  }
}
