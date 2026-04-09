package com.kipu.core.contacts.domain.port.out;

import java.util.Optional;
import java.util.UUID;

public interface SelfContactLookupPort {

  Optional<UUID> findSelfContactId(UUID userId);
}
