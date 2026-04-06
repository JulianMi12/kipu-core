package com.kipu.core.contacts.application.create;

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
public class CreateContactUseCase {

  private final ContactRepository contactRepository;

  public CreateContactResult execute(CreateContactCommand command) {
    log.info(
        "[CreateContactUseCase] Creating contact for owner user id: {}", command.ownerUserId());

    Contact contact =
        Contact.createSelfContact(
            command.ownerUserId(),
            command.firstName(),
            command.lastName(),
            command.primaryEmail(),
            command.birthdate(),
            command.dynamicAttributes());

    contactRepository.save(contact);

    log.info(
        "[CreateContactUseCase] Contact created with id: {} for owner user id: {}",
        contact.getId(),
        command.ownerUserId());
    return new CreateContactResult(contact.getId());
  }
}
