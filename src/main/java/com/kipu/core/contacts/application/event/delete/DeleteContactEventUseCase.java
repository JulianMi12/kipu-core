package com.kipu.core.contacts.application.event.delete;

import com.kipu.core.contacts.domain.exception.ContactEventNotFoundException;
import com.kipu.core.contacts.domain.exception.ContactNotFoundException;
import com.kipu.core.contacts.domain.exception.UnauthorizedContactAccessException;
import com.kipu.core.contacts.domain.model.Contact;
import com.kipu.core.contacts.domain.model.ContactEvent;
import com.kipu.core.contacts.domain.repository.ContactEventRepository;
import com.kipu.core.contacts.domain.repository.ContactRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DeleteContactEventUseCase {

  private final ContactRepository contactRepository;
  private final ContactEventRepository contactEventRepository;

  public void execute(UUID authenticatedUserId, UUID eventId) {
    log.info("[DeleteContactEventUseCase] Starting process with id: {}", eventId);

    ContactEvent event =
        contactEventRepository
            .findById(eventId)
            .orElseThrow(() -> new ContactEventNotFoundException(eventId));

    Contact contact =
        contactRepository
            .findById(event.getContactId())
            .orElseThrow(() -> new ContactNotFoundException(event.getContactId()));

    if (!contact.getOwnerUserId().equals(authenticatedUserId)) {
      log.error(
          "[DeleteContactEventUseCase] Error occurred during authorization: user {} does not own contact {}",
          authenticatedUserId,
          event.getContactId());
      throw new UnauthorizedContactAccessException();
    }

    contactEventRepository.delete(eventId);

    log.info("[DeleteContactEventUseCase] Process completed successfully for id: {}", eventId);
  }
}
