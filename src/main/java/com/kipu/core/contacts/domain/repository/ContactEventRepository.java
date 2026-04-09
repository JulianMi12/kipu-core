package com.kipu.core.contacts.domain.repository;

import com.kipu.core.contacts.domain.model.ContactEvent;
import java.util.Optional;
import java.util.UUID;

public interface ContactEventRepository {

  ContactEvent save(ContactEvent event);

  Optional<ContactEvent> findById(UUID id);

  Optional<ContactEvent> findByIdWithTags(UUID id);

  void delete(UUID id);

  Optional<ContactEvent> findByContactIdAndTagIdsContains(UUID contactId, UUID tagId);
}
