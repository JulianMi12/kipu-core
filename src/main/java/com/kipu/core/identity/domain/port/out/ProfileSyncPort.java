package com.kipu.core.identity.domain.port.out;

import java.time.LocalDate;
import java.util.UUID;

public interface ProfileSyncPort {

  UUID createSelfContact(
      UUID userId, String firstName, String lastName, String email, LocalDate birthdate);
}
