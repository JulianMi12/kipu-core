package com.kipu.core.contacts.application.contact.create;

import com.kipu.core.contacts.application.contact.get.ContactSummaryResult;
import com.kipu.core.contacts.application.event.birthday.EnsureBirthdayEventUseCase;
import com.kipu.core.contacts.domain.exception.ContactFirstNameRequiredException;
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
public class CreateContactUseCase {

  private static final String BIRTHDAY_TAG_NAME = "Cumpleaños";

  private final UserTagRepository tagRepository;
  private final ContactRepository contactRepository;
  private final EnsureBirthdayEventUseCase ensureBirthdayEventUseCase;

  public ContactSummaryResult execute(CreateContactCommand command) {
    log.info("[CreateContactUseCase] Creating contact for owner: {}", command.ownerUserId());

    validateCommand(command);

    Contact contact = createDomainObject(command);
    contactRepository.save(contact);

    if (contact.getBirthdate() != null) {
      automateBirthdayEvent(contact, command.timezone());
    }

    log.info("[CreateContactUseCase] Contact created successfully with id: {}", contact.getId());
    return ContactSummaryResult.from(contact);
  }

  private void validateCommand(CreateContactCommand command) {
    if (command.firstName() == null || command.firstName().isBlank()) {
      throw new ContactFirstNameRequiredException();
    }
  }

  private Contact createDomainObject(CreateContactCommand command) {
    return Contact.create(
        command.ownerUserId(),
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
        .filter(tag -> contact.getTagIds().contains(tag.getId()))
        .ifPresent(
            tag -> ensureBirthdayEventUseCase.execute(contact, tag.getId(), timezone, false));
  }
}
