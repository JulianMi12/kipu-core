package com.kipu.core.contacts.application.event.complete;

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
public class CompleteContactEventUseCase {

  private final ContactRepository contactRepository;
  private final ContactEventRepository contactEventRepository;

  public CompleteContactEventResult execute(CompleteContactEventCommand command) {
    log.info("[CompleteContactEventUseCase] Starting process with id: {}", command.eventId());

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
          "[CompleteContactEventUseCase] Error occurred during authorization: user {} does not own contact {}",
          command.authenticatedUserId(),
          event.getContactId());
      throw new UnauthorizedContactAccessException();
    }

    event.complete();

    ContactEvent savedEvent = contactEventRepository.save(event);

    log.info(
        "[CompleteContactEventUseCase] Process completed successfully for id: {}",
        command.eventId());
    return CompleteContactEventResult.from(savedEvent);
  }
}
