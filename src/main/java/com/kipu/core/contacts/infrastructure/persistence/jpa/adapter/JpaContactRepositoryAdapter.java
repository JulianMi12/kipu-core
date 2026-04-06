package com.kipu.core.contacts.infrastructure.persistence.jpa.adapter;

import com.kipu.core.contacts.domain.model.Contact;
import com.kipu.core.contacts.domain.repository.ContactRepository;
import com.kipu.core.contacts.infrastructure.persistence.jpa.entity.ContactJpaEntity;
import com.kipu.core.contacts.infrastructure.persistence.jpa.repository.JpaContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaContactRepositoryAdapter implements ContactRepository {

  private final JpaContactRepository jpaContactRepository;

  @Override
  public void save(Contact contact) {
    jpaContactRepository.save(ContactJpaEntity.fromDomain(contact));
  }
}
