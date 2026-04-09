package com.kipu.core.contacts.infrastructure.rest.controller.impl;

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
import com.kipu.core.contacts.infrastructure.rest.controller.ContactEventApi;
import com.kipu.core.contacts.infrastructure.rest.dto.CreateContactEventRequest;
import com.kipu.core.contacts.infrastructure.rest.dto.UpdateContactEventRequest;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/contacts/{contactId}/events")
@RequiredArgsConstructor
public class ContactEventController implements ContactEventApi {

  private final CreateContactEventUseCase createContactEventUseCase;
  private final UpdateContactEventUseCase updateContactEventUseCase;
  private final DeleteContactEventUseCase deleteContactEventUseCase;
  private final CompleteContactEventUseCase completeContactEventUseCase;
  private final UndoContactEventUseCase undoContactEventUseCase;

  @Override
  @PostMapping
  public ResponseEntity<CreateContactEventResult> createEvent(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID contactId,
      @RequestBody CreateContactEventRequest request) {
    log.info(
        "[ContactEventController] POST /contacts/{}/events called for user id: {}",
        contactId,
        userId);
    CreateContactEventResult result =
        createContactEventUseCase.execute(
            new CreateContactEventCommand(
                userId,
                contactId,
                request.title(),
                request.description(),
                request.startDateTime(),
                request.alertLeadTimeDays(),
                request.recurrenceType(),
                request.recurrenceInterval(),
                request.timezone(),
                request.tagIds() != null ? request.tagIds() : Set.of()));
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }

  @Override
  @PutMapping("/{eventId}")
  public ResponseEntity<CreateContactEventResult> updateEvent(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID contactId,
      @PathVariable UUID eventId,
      @RequestBody UpdateContactEventRequest request) {
    log.info(
        "[ContactEventController] PUT /contacts/{}/events/{} called for user id: {}",
        contactId,
        eventId,
        userId);
    CreateContactEventResult result =
        updateContactEventUseCase.execute(
            new UpdateContactEventCommand(
                userId,
                eventId,
                request.title(),
                request.description(),
                request.startDateTime(),
                request.alertLeadTimeDays(),
                request.recurrenceType(),
                request.recurrenceInterval(),
                request.timezone(),
                request.tagIds() != null ? request.tagIds() : Set.of()));
    return ResponseEntity.ok(result);
  }

  @Override
  @DeleteMapping("/{eventId}")
  public ResponseEntity<Void> deleteEvent(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID contactId,
      @PathVariable UUID eventId) {
    log.info(
        "[ContactEventController] DELETE /contacts/{}/events/{} called for user id: {}",
        contactId,
        eventId,
        userId);
    deleteContactEventUseCase.execute(userId, eventId);
    return ResponseEntity.noContent().build();
  }

  @Override
  @PatchMapping("/{eventId}/complete")
  public ResponseEntity<CompleteContactEventResult> completeEvent(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID contactId,
      @PathVariable UUID eventId) {
    log.info(
        "[ContactEventController] PATCH /contacts/{}/events/{}/complete called for user id: {}",
        contactId,
        eventId,
        userId);
    CompleteContactEventResult result =
        completeContactEventUseCase.execute(new CompleteContactEventCommand(userId, eventId));
    return ResponseEntity.ok(result);
  }

  @Override
  @PatchMapping("/{eventId}/undo")
  public ResponseEntity<CompleteContactEventResult> undoEvent(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID contactId,
      @PathVariable UUID eventId) {
    log.info(
        "[ContactEventController] PATCH /contacts/{}/events/{}/undo called for user id: {}",
        contactId,
        eventId,
        userId);
    CompleteContactEventResult result = undoContactEventUseCase.execute(userId, eventId);
    return ResponseEntity.ok(result);
  }
}
