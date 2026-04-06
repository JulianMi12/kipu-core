package com.kipu.core.identity.infrastructure.contacts;

import com.kipu.core.contacts.application.create.CreateContactCommand;
import com.kipu.core.contacts.application.create.CreateContactResult;
import com.kipu.core.contacts.application.create.CreateContactUseCase;
import com.kipu.core.identity.domain.port.out.ProfileSyncPort;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContactsProfileSyncAdapter implements ProfileSyncPort {

  private final CreateContactUseCase createContactUseCase;

  @Override
  public UUID createSelfContact(
      UUID userId, String firstName, String lastName, String email, LocalDate birthdate) {
    log.info("[ContactsProfileSyncAdapter] Creating self-contact for user id: {}", userId);

    CreateContactResult result =
        createContactUseCase.execute(
            new CreateContactCommand(userId, firstName, lastName, email, birthdate, Map.of()));

    log.info(
        "[ContactsProfileSyncAdapter] Self-contact created with id: {} for user id: {}",
        result.contactId(),
        userId);
    return result.contactId();
  }
}
