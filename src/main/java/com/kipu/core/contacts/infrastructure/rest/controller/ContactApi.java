package com.kipu.core.contacts.infrastructure.rest.controller;

import com.kipu.core.contacts.application.contact.get.ContactDetailResult;
import com.kipu.core.contacts.application.contact.get.ContactSummaryResult;
import com.kipu.core.contacts.infrastructure.rest.dto.ContactRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Contacts", description = "Manage contacts")
@SecurityRequirement(name = "bearerAuth")
public interface ContactApi {

  @Operation(summary = "Create a new contact")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Contact created successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid request body"),
    @ApiResponse(responseCode = "401", description = "Missing or invalid access token")
  })
  ResponseEntity<ContactSummaryResult> createContact(
      @AuthenticationPrincipal UUID userId, @Valid @RequestBody ContactRequest request);

  @Operation(summary = "Update an existing contact")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Contact updated successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid request body"),
    @ApiResponse(responseCode = "401", description = "Missing or invalid access token"),
    @ApiResponse(responseCode = "403", description = "Access denied to this contact"),
    @ApiResponse(responseCode = "404", description = "Contact not found")
  })
  ResponseEntity<ContactSummaryResult> updateContact(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID contactId,
      @Valid @RequestBody ContactRequest request);

  @Operation(
      summary = "List contacts for the authenticated user",
      description =
          "Paginated. Supports ?page=0&size=20&sort=createdAt,desc. "
              + "Each item includes up to 3 tagIds.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Contacts retrieved successfully"),
    @ApiResponse(responseCode = "401", description = "Missing or invalid access token")
  })
  ResponseEntity<Page<ContactSummaryResult>> getContacts(
      @AuthenticationPrincipal UUID userId, UUID selfContactId, Pageable pageable);

  @Operation(summary = "Delete a contact")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Contact deleted successfully"),
    @ApiResponse(responseCode = "401", description = "Missing or invalid access token"),
    @ApiResponse(responseCode = "403", description = "Access denied to this contact"),
    @ApiResponse(responseCode = "404", description = "Contact not found"),
    @ApiResponse(responseCode = "409", description = "Cannot delete the self-contact")
  })
  ResponseEntity<Void> deleteContact(
      @AuthenticationPrincipal UUID userId, @PathVariable UUID contactId);

  @Operation(summary = "Get full detail of a contact")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Contact retrieved successfully"),
    @ApiResponse(responseCode = "401", description = "Missing or invalid access token"),
    @ApiResponse(responseCode = "403", description = "Access denied to this contact"),
    @ApiResponse(responseCode = "404", description = "Contact not found")
  })
  ResponseEntity<ContactDetailResult> getContact(
      @AuthenticationPrincipal UUID userId, @PathVariable UUID contactId);
}
