package com.kipu.core.identity.domain.port.out;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface ProfileSyncPort {

  ContactProfileInfo createSelfContact(
      UUID userId, String firstName, String lastName, String email, LocalDate birthdate);

  Optional<ContactProfileInfo> getContactById(UUID contactId);
}
