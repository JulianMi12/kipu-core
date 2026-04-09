package com.kipu.core.contacts.application.contact.delete;

import com.kipu.core.contacts.domain.exception.CannotDeleteSelfContactException;
import com.kipu.core.contacts.domain.exception.ContactNotFoundException;
import com.kipu.core.contacts.domain.exception.UnauthorizedContactAccessException;
import com.kipu.core.contacts.domain.model.Contact;
import com.kipu.core.contacts.domain.port.out.SelfContactLookupPort;
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
public class DeleteContactUseCase {

  private final ContactRepository contactRepository;
  private final SelfContactLookupPort selfContactLookupPort;

  public void execute(DeleteContactCommand command) {
    log.info("[DeleteContactUseCase] Starting process with id: {}", command.contactId());

    Contact contact =
        contactRepository
            .findById(command.contactId())
            .orElseThrow(() -> new ContactNotFoundException(command.contactId()));

    if (!contact.getOwnerUserId().equals(command.authenticatedUserId())) {
      log.error(
          "[DeleteContactUseCase] Error occurred during authorization: user {} does not own contact {}",
          command.authenticatedUserId(),
          command.contactId());
      throw new UnauthorizedContactAccessException();
    }

    UUID selfContactId =
        selfContactLookupPort.findSelfContactId(command.authenticatedUserId()).orElse(null);

    if (command.contactId().equals(selfContactId)) {
      log.error(
          "[DeleteContactUseCase] Error occurred during deletion: contact {} is the self-contact of user {}",
          command.contactId(),
          command.authenticatedUserId());
      throw new CannotDeleteSelfContactException();
    }

    contactRepository.delete(command.contactId());

    log.info(
        "[DeleteContactUseCase] Process completed successfully for id: {}", command.contactId());
  }
}
