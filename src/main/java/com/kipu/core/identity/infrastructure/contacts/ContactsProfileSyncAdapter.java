package com.kipu.core.identity.infrastructure.contacts;

import com.kipu.core.contacts.application.contact.create.CreateContactCommand;
import com.kipu.core.contacts.application.contact.create.CreateContactResult;
import com.kipu.core.contacts.application.contact.create.CreateContactUseCase;
import com.kipu.core.contacts.application.tag.create.CreateUserTagCommand;
import com.kipu.core.contacts.application.tag.create.CreateUserTagResult;
import com.kipu.core.contacts.application.tag.create.CreateUserTagUseCase;
import com.kipu.core.contacts.domain.repository.ContactRepository;
import com.kipu.core.identity.domain.port.out.ContactProfileInfo;
import com.kipu.core.identity.domain.port.out.ProfileSyncPort;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContactsProfileSyncAdapter implements ProfileSyncPort {

  private static final String DEFAULT_TAG_NAME = "personal";
  private static final String DEFAULT_TAG_COLOR = "#4F46E5";

  private final CreateContactUseCase createContactUseCase;
  private final ContactRepository contactRepository;
  private final CreateUserTagUseCase createUserTagUseCase;

  @Override
  public ContactProfileInfo createSelfContact(
      UUID userId, String firstName, String lastName, String email, LocalDate birthdate) {
    log.info("[ContactsProfileSyncAdapter] Creating self-contact for user id: {}", userId);

    CreateUserTagResult personalTag =
        createUserTagUseCase.execute(
            new CreateUserTagCommand(userId, DEFAULT_TAG_NAME, DEFAULT_TAG_COLOR));
    log.info(
        "[ContactsProfileSyncAdapter] Created Personal tag with id: {} for user id: {}",
        personalTag.tagId(),
        userId);

    CreateContactResult result =
        createContactUseCase.execute(
            new CreateContactCommand(
                userId,
                firstName,
                lastName,
                email,
                birthdate,
                Map.of(),
                Set.of(personalTag.tagId())));

    log.info(
        "[ContactsProfileSyncAdapter] Self-contact created with id: {} for user id: {}",
        result.contactId(),
        userId);
    return new ContactProfileInfo(result.contactId(), firstName, lastName);
  }

  @Override
  public Optional<ContactProfileInfo> getContactById(UUID contactId) {
    log.debug("[ContactsProfileSyncAdapter] Fetching contact by id: {}", contactId);
    return contactRepository
        .findByIdWithTags(contactId)
        .map(c -> new ContactProfileInfo(c.getId(), c.getFirstName(), c.getLastName()));
  }
}
