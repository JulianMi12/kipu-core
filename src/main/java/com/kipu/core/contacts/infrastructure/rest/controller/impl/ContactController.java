package com.kipu.core.contacts.infrastructure.rest.controller.impl;

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
import com.kipu.core.contacts.infrastructure.rest.controller.ContactApi;
import com.kipu.core.contacts.infrastructure.rest.dto.ContactRequest;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/contacts")
@RequiredArgsConstructor
public class ContactController implements ContactApi {

  private final CreateContactUseCase createContactUseCase;
  private final UpdateContactUseCase updateContactUseCase;
  private final DeleteContactUseCase deleteContactUseCase;
  private final GetUserContactsUseCase getUserContactsUseCase;
  private final GetContactDetailUseCase getContactDetailUseCase;

  @Override
  @PostMapping
  public ResponseEntity<ContactSummaryResult> createContact(
      @AuthenticationPrincipal UUID userId, @RequestBody ContactRequest request) {
    log.info("[ContactController] POST /contacts called for user id: {}", userId);
    ContactSummaryResult result =
        createContactUseCase.execute(
            new CreateContactCommand(
                userId,
                request.firstName(),
                request.lastName(),
                request.primaryEmail(),
                request.birthdate(),
                request.dynamicAttributes() != null ? request.dynamicAttributes() : Map.of(),
                request.tagIds() != null ? request.tagIds() : Set.of()));
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }

  @Override
  @PutMapping("/{contactId}")
  public ResponseEntity<ContactSummaryResult> updateContact(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID contactId,
      @RequestBody ContactRequest request) {
    log.info("[ContactController] PUT /contacts/{} called for user id: {}", contactId, userId);
    ContactSummaryResult result =
        updateContactUseCase.execute(
            new UpdateContactCommand(
                userId,
                contactId,
                request.firstName(),
                request.lastName(),
                request.primaryEmail(),
                request.birthdate(),
                request.dynamicAttributes() != null ? request.dynamicAttributes() : Map.of(),
                request.tagIds() != null ? request.tagIds() : Set.of()));
    return ResponseEntity.ok(result);
  }

  @Override
  @DeleteMapping("/{contactId}")
  public ResponseEntity<Void> deleteContact(
      @AuthenticationPrincipal UUID userId, @PathVariable UUID contactId) {
    log.info("[ContactController] DELETE /contacts/{} called for user id: {}", contactId, userId);
    deleteContactUseCase.execute(new DeleteContactCommand(userId, contactId));
    return ResponseEntity.noContent().build();
  }

  @Override
  @GetMapping
  public ResponseEntity<Page<ContactSummaryResult>> getContacts(
      @AuthenticationPrincipal UUID userId,
      @RequestParam(required = false) UUID selfContactId,
      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    log.info("[ContactController] GET /contacts called for user id: {}", userId);
    return ResponseEntity.ok(getUserContactsUseCase.execute(userId, selfContactId, pageable));
  }

  @Override
  @GetMapping("/{contactId}")
  public ResponseEntity<ContactDetailResult> getContact(
      @AuthenticationPrincipal UUID userId, @PathVariable UUID contactId) {
    log.info("[ContactController] GET /contacts/{} called for user id: {}", contactId, userId);
    return ResponseEntity.ok(getContactDetailUseCase.execute(userId, contactId));
  }
}
