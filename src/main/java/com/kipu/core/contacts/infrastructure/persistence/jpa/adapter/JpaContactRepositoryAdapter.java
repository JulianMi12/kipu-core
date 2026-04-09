package com.kipu.core.contacts.infrastructure.persistence.jpa.adapter;

import com.kipu.core.contacts.domain.model.Contact;
import com.kipu.core.contacts.domain.repository.ContactRepository;
import com.kipu.core.contacts.infrastructure.persistence.jpa.entity.ContactJpaEntity;
import com.kipu.core.contacts.infrastructure.persistence.jpa.repository.JpaContactRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

  @Override
  public void delete(UUID id) {
    jpaContactRepository.deleteById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<Contact> findAllByOwnerUserId(
      UUID ownerUserId, UUID excludedContactId, Pageable pageable) {
    if (excludedContactId == null) {
      return jpaContactRepository
          .findByOwnerUserIdOrderByCreatedAtDesc(ownerUserId, pageable)
          .map(ContactJpaEntity::toDomain);
    }
    return jpaContactRepository
        .findAllByOwnerUserIdAndIdNot(ownerUserId, excludedContactId, pageable)
        .map(ContactJpaEntity::toDomain);
  }
}
