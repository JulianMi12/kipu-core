package com.kipu.core.contacts.application.event.update;

import com.kipu.core.contacts.application.event.create.CreateContactEventResult;
import com.kipu.core.contacts.domain.exception.ContactEventNotFoundException;
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
public class UpdateContactEventUseCase {

  private final ContactRepository contactRepository;
  private final ContactEventRepository contactEventRepository;

  public CreateContactEventResult execute(UpdateContactEventCommand command) {
    log.info("[UpdateContactEventUseCase] Starting process with id: {}", command.eventId());

    ContactEvent event =
        contactEventRepository
            .findById(command.eventId())
            .orElseThrow(() -> new ContactEventNotFoundException(command.eventId()));

    Contact contact =
        contactRepository
            .findById(event.getContactId())
            .orElseThrow(() -> new ContactNotFoundException(event.getContactId()));

    if (!contact.getOwnerUserId().equals(command.authenticatedUserId())) {
      log.error(
          "[UpdateContactEventUseCase] Error occurred during authorization: user {} does not own contact {}",
          command.authenticatedUserId(),
          event.getContactId());
      throw new UnauthorizedContactAccessException();
    }

    event.update(
        command.title(),
        command.description(),
        command.startDateTime(),
        command.alertLeadTimeDays(),
        command.recurrenceType(),
        command.recurrenceInterval(),
        command.timezone(),
        command.tagIds());

    ContactEvent updatedEvent = contactEventRepository.save(event);

    log.info(
        "[UpdateContactEventUseCase] Process completed successfully for id: {}", command.eventId());
    return CreateContactEventResult.from(updatedEvent);
  }
}
