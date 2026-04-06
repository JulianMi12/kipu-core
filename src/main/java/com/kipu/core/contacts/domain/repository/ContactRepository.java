package com.kipu.core.contacts.domain.repository;

import com.kipu.core.contacts.domain.model.Contact;
import java.util.Optional;
import java.util.UUID;

public interface ContactRepository {

  void save(Contact contact);

  Optional<Contact> findById(UUID id);
}
