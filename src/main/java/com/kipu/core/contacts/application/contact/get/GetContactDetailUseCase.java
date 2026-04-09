package com.kipu.core.contacts.application.contact.get;

import com.kipu.core.contacts.domain.exception.ContactNotFoundException;
import com.kipu.core.contacts.domain.exception.UnauthorizedContactAccessException;
import com.kipu.core.contacts.domain.model.Contact;
import com.kipu.core.contacts.domain.repository.ContactRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetContactDetailUseCase {

  private final ContactRepository contactRepository;

  public ContactDetailResult execute(UUID authenticatedUserId, UUID contactId) {
    log.info("[GetContactDetailUseCase] Starting process with id: {}", contactId);

    Contact contact =
        contactRepository
            .findByIdWithTags(contactId)
            .orElseThrow(() -> new ContactNotFoundException(contactId));

    if (!contact.getOwnerUserId().equals(authenticatedUserId)) {
      log.error(
          "[GetContactDetailUseCase] Error occurred during authorization: user {} does not own contact {}",
          authenticatedUserId,
          contactId);
      throw new UnauthorizedContactAccessException();
    }

    log.info("[GetContactDetailUseCase] Process completed successfully for id: {}", contactId);
    return ContactDetailResult.from(contact);
  }
}
