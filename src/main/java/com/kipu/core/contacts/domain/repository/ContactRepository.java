package com.kipu.core.contacts.domain.repository;

import com.kipu.core.contacts.domain.model.Contact;

public interface ContactRepository {

  void save(Contact contact);
}
