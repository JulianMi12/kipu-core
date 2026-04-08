package com.kipu.core.contacts.infrastructure.rest.controller;

import com.kipu.core.contacts.application.event.complete.CompleteContactEventResult;
import com.kipu.core.contacts.application.event.create.CreateContactEventResult;
import com.kipu.core.contacts.infrastructure.rest.dto.CreateContactEventRequest;
import com.kipu.core.contacts.infrastructure.rest.dto.UpdateContactEventRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Contact Events", description = "Manage events linked to contacts")
@SecurityRequirement(name = "bearerAuth")
public interface ContactEventApi {

  @Operation(summary = "Create a new event for a contact")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Event created successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid request body"),
    @ApiResponse(responseCode = "401", description = "Missing or invalid access token"),
    @ApiResponse(responseCode = "403", description = "Access denied to this contact"),
    @ApiResponse(responseCode = "404", description = "Contact not found")
  })
  ResponseEntity<CreateContactEventResult> createEvent(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID contactId,
      @Valid @RequestBody CreateContactEventRequest request);

  @Operation(summary = "Update an existing contact event")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Event updated successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid request body"),
    @ApiResponse(responseCode = "401", description = "Missing or invalid access token"),
    @ApiResponse(responseCode = "403", description = "Access denied to this contact"),
    @ApiResponse(responseCode = "404", description = "Event not found")
  })
  ResponseEntity<CreateContactEventResult> updateEvent(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID contactId,
      @PathVariable UUID eventId,
      @Valid @RequestBody UpdateContactEventRequest request);

  @Operation(summary = "Delete a contact event")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Event deleted successfully"),
    @ApiResponse(responseCode = "401", description = "Missing or invalid access token"),
    @ApiResponse(responseCode = "403", description = "Access denied to this contact"),
    @ApiResponse(responseCode = "404", description = "Event not found")
  })
  ResponseEntity<Void> deleteEvent(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID contactId,
      @PathVariable UUID eventId);

  @Operation(summary = "Complete a contact event, advancing its recurrence if applicable")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Event completed successfully"),
    @ApiResponse(responseCode = "401", description = "Missing or invalid access token"),
    @ApiResponse(responseCode = "403", description = "Access denied to this contact"),
    @ApiResponse(responseCode = "404", description = "Event not found")
  })
  ResponseEntity<CompleteContactEventResult> completeEvent(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID contactId,
      @PathVariable UUID eventId);

  @Operation(summary = "Undo the last completion of a contact event")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Event undone successfully"),
    @ApiResponse(responseCode = "401", description = "Missing or invalid access token"),
    @ApiResponse(responseCode = "403", description = "Access denied to this contact"),
    @ApiResponse(responseCode = "404", description = "Event not found")
  })
  ResponseEntity<CompleteContactEventResult> undoEvent(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID contactId,
      @PathVariable UUID eventId);
}
