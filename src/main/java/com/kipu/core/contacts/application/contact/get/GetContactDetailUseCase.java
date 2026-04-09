package com.kipu.core.contacts.application.contact.get;

import com.kipu.core.contacts.domain.exception.ContactNotFoundException;
import com.kipu.core.contacts.domain.exception.UnauthorizedContactAccessException;
import com.kipu.core.contacts.domain.model.Contact;
import com.kipu.core.contacts.domain.model.UserTag;
import com.kipu.core.contacts.domain.repository.ContactRepository;
import com.kipu.core.contacts.domain.repository.UserTagRepository;
import java.util.List;
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
  private final UserTagRepository userTagRepository;

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

    List<UserTag> tags = List.of();
    if (contact.getTagIds() != null && !contact.getTagIds().isEmpty()) {
      tags = userTagRepository.findAllById(contact.getTagIds());
    }

    log.info("[GetContactDetailUseCase] Process completed successfully for id: {}", contactId);
    return ContactDetailResult.from(contact, tags);
  }
}
