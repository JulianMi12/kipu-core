package com.kipu.core.contacts.domain.repository;

import com.kipu.core.contacts.domain.model.Contact;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContactRepository {

  void save(Contact contact);

  Optional<Contact> findById(UUID id);

  Optional<Contact> findByIdWithTags(UUID id);

  Page<Contact> findAllByOwnerUserId(UUID ownerUserId, UUID excludedContactId, Pageable pageable);

  void delete(UUID id);
}
