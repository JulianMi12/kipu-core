package com.kipu.core.identity.infrastructure.contacts;

import com.kipu.core.contacts.application.contact.create.CreateContactCommand;
import com.kipu.core.contacts.application.contact.create.CreateContactUseCase;
import com.kipu.core.contacts.application.contact.get.ContactSummaryResult;
import com.kipu.core.contacts.application.event.birthday.EnsureBirthdayEventUseCase;
import com.kipu.core.contacts.application.tag.create.CreateUserTagCommand;
import com.kipu.core.contacts.application.tag.create.CreateUserTagUseCase;
import com.kipu.core.contacts.domain.model.Contact;
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

  private static final String PERSONAL_TAG = "Personal";
  private static final String BIRTHDAY_TAG = "Cumpleaños";
  private static final String PRIMARY_COLOR = "#4F46E5";
  private static final String ACCENT_COLOR = "#EC4899";

  private final ContactRepository contactRepository;
  private final CreateContactUseCase createContactUseCase;
  private final CreateUserTagUseCase createUserTagUseCase;
  private final EnsureBirthdayEventUseCase ensureBirthdayEventUseCase;

  @Override
  public ContactProfileInfo createSelfContact(
      UUID userId, String first, String last, String email, LocalDate birth, String timeZone) {

    log.info("[ContactsProfileSyncAdapter] Syncing self-contact for user: {}", userId);
    UUID personalTagId = createTag(userId, PERSONAL_TAG, PRIMARY_COLOR);
    UUID birthdayTagId = createTag(userId, BIRTHDAY_TAG, ACCENT_COLOR);

    ContactSummaryResult result =
        createContactUseCase.execute(
            new CreateContactCommand(
                userId,
                first,
                last,
                email,
                birth,
                Map.of(),
                Set.of(personalTagId, birthdayTagId),
                timeZone));

    automateBirthday(userId, birth, timeZone, result, birthdayTagId, personalTagId);

    return new ContactProfileInfo(result.id(), result.firstName(), result.lastName());
  }

  private UUID createTag(UUID userId, String name, String color) {
    return createUserTagUseCase.execute(new CreateUserTagCommand(userId, name, color)).tagId();
  }

  private void automateBirthday(
      UUID userId,
      LocalDate birth,
      String timeZone,
      ContactSummaryResult res,
      UUID bDayTag,
      UUID pTag) {
    if (birth == null) return;

    String effectiveTz = (timeZone != null && !timeZone.isBlank()) ? timeZone : "UTC";

    Contact contact =
        Contact.reconstitute(
            res.id(),
            userId,
            res.firstName(),
            res.lastName(),
            res.primaryEmail(),
            birth,
            Map.of(),
            Set.of(pTag, bDayTag),
            null);

    ensureBirthdayEventUseCase.execute(contact, bDayTag, effectiveTz, true);
  }

  @Override
  public Optional<ContactProfileInfo> getContactById(UUID contactId) {
    return contactRepository
        .findById(contactId)
        .map(c -> new ContactProfileInfo(c.getId(), c.getFirstName(), c.getLastName()));
  }
}
