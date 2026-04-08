package com.kipu.core.contacts.infrastructure.persistence.jpa.adapter;

import com.kipu.core.contacts.domain.model.Contact;
import com.kipu.core.contacts.domain.repository.ContactRepository;
import com.kipu.core.contacts.infrastructure.persistence.jpa.entity.ContactJpaEntity;
import com.kipu.core.contacts.infrastructure.persistence.jpa.repository.JpaContactRepository;
import java.util.Optional;
import java.util.UUID;
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

  @Override
  public Optional<Contact> findById(UUID id) {
    return jpaContactRepository.findById(id).map(ContactJpaEntity::toDomain);
  }

  @Override
  public Optional<Contact> findByIdWithTags(UUID id) {
    return jpaContactRepository.findByIdWithTags(id).map(ContactJpaEntity::toDomain);
  }
}
