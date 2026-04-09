package com.kipu.core.contacts.application.event.create;

import com.kipu.core.contacts.domain.exception.ContactNotFoundException;
import com.kipu.core.contacts.domain.exception.UnauthorizedContactAccessException;
import com.kipu.core.contacts.domain.model.Contact;
import com.kipu.core.contacts.domain.model.ContactEvent;
import com.kipu.core.contacts.domain.repository.ContactEventRepository;
import com.kipu.core.contacts.domain.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CreateContactEventUseCase {

  private final ContactRepository contactRepository;
  private final ContactEventRepository contactEventRepository;

  public CreateContactEventResult execute(CreateContactEventCommand command) {
    log.info(
        "[CreateContactEventUseCase] Starting process with contactId: {}", command.contactId());

    Contact contact =
        contactRepository
            .findById(command.contactId())
            .orElseThrow(() -> new ContactNotFoundException(command.contactId()));

    if (!contact.getOwnerUserId().equals(command.authenticatedUserId())) {
      log.error(
          "[CreateContactEventUseCase] Error occurred during authorization: user {} does not own contact {}",
          command.authenticatedUserId(),
          command.contactId());
      throw new UnauthorizedContactAccessException();
    }

    ContactEvent event =
        ContactEvent.create(
            command.contactId(),
            command.title(),
            command.description(),
            command.startDateTime(),
            command.alertLeadTimeDays(),
            command.recurrenceType(),
            command.recurrenceInterval(),
            command.timezone(),
            command.tagIds());

    ContactEvent savedEvent = contactEventRepository.save(event);

    log.info(
        "[CreateContactEventUseCase] Event created successfully with id: {} for contact: {}",
        savedEvent.getId(),
        command.contactId());

    return CreateContactEventResult.from(savedEvent);
  }
}
