package com.kipu.core.contacts.application.contact.update;

import com.kipu.core.contacts.application.contact.get.ContactSummaryResult;
import com.kipu.core.contacts.domain.exception.ContactNotFoundException;
import com.kipu.core.contacts.domain.exception.UnauthorizedContactAccessException;
import com.kipu.core.contacts.domain.model.Contact;
import com.kipu.core.contacts.domain.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UpdateContactUseCase {

  private final ContactRepository contactRepository;

  public ContactSummaryResult execute(UpdateContactCommand command) {
    log.info("[UpdateContactUseCase] Starting process with id: {}", command.contactId());

    Contact contact =
        contactRepository
            .findByIdWithTags(command.contactId())
            .orElseThrow(() -> new ContactNotFoundException(command.contactId()));

    if (!contact.getOwnerUserId().equals(command.authenticatedUserId())) {
      log.error(
          "[UpdateContactUseCase] Error occurred during authorization: user {} does not own contact {}",
          command.authenticatedUserId(),
          command.contactId());
      throw new UnauthorizedContactAccessException();
    }

    contact.update(
        command.firstName(),
        command.lastName(),
        command.primaryEmail(),
        command.birthdate(),
        command.dynamicAttributes(),
        command.tagIds());

    contactRepository.save(contact);

    log.info(
        "[UpdateContactUseCase] Process completed successfully for id: {}", command.contactId());
    return ContactSummaryResult.from(contact);
  }
}
