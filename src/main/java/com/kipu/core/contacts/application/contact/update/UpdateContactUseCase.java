package com.kipu.core.contacts.application.contact.update;

import com.kipu.core.contacts.application.contact.get.ContactSummaryResult;
import com.kipu.core.contacts.application.event.birthday.EnsureBirthdayEventUseCase;
import com.kipu.core.contacts.domain.exception.ContactNotFoundException;
import com.kipu.core.contacts.domain.exception.UnauthorizedContactAccessException;
import com.kipu.core.contacts.domain.model.Contact;
import com.kipu.core.contacts.domain.repository.ContactRepository;
import com.kipu.core.contacts.domain.repository.UserTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UpdateContactUseCase {

  private static final String BIRTHDAY_TAG_NAME = "Cumpleaños";

  private final ContactRepository contactRepository;
  private final UserTagRepository tagRepository;
  private final EnsureBirthdayEventUseCase ensureBirthdayEventUseCase;

  public ContactSummaryResult execute(UpdateContactCommand command) {
    log.info("[UpdateContactUseCase] Updating contact: {}", command.contactId());

    Contact contact = findAndValidateContact(command);

    updateDomainObject(contact, command);
    contactRepository.save(contact);

    if (contact.getBirthdate() != null) {
      automateBirthdayEvent(contact, command.timezone());
    }

    log.info("[UpdateContactUseCase] Update successful for contact: {}", contact.getId());
    return ContactSummaryResult.from(contact);
  }

  private Contact findAndValidateContact(UpdateContactCommand command) {
    Contact contact =
        contactRepository
            .findByIdWithTags(command.contactId())
            .orElseThrow(() -> new ContactNotFoundException(command.contactId()));

    if (!contact.getOwnerUserId().equals(command.authenticatedUserId())) {
      log.error(
          "[UpdateContactUseCase] Authorization failed: user {} doesn't own contact {}",
          command.authenticatedUserId(),
          command.contactId());
      throw new UnauthorizedContactAccessException();
    }
    return contact;
  }

  private void updateDomainObject(Contact contact, UpdateContactCommand command) {
    contact.update(
        command.firstName(),
        command.lastName(),
        command.primaryEmail(),
        command.birthdate(),
        command.dynamicAttributes(),
        command.tagIds());
  }

  private void automateBirthdayEvent(Contact contact, String timezone) {
    tagRepository
        .findByOwnerUserIdAndNameIgnoreCase(contact.getOwnerUserId(), BIRTHDAY_TAG_NAME)
        .ifPresent(
            tag -> ensureBirthdayEventUseCase.execute(contact, tag.getId(), timezone, false));
  }
}
