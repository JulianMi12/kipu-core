package com.kipu.core.contacts.infrastructure.persistence.jpa.adapter;

import com.kipu.core.contacts.domain.model.ContactEvent;
import com.kipu.core.contacts.domain.repository.ContactEventRepository;
import com.kipu.core.contacts.infrastructure.persistence.jpa.entity.ContactEventJpaEntity;
import com.kipu.core.contacts.infrastructure.persistence.jpa.repository.JpaContactEventRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaContactEventRepositoryAdapter implements ContactEventRepository {

  private final JpaContactEventRepository jpaContactEventRepository;

  @Override
  public ContactEvent save(ContactEvent event) {
    return jpaContactEventRepository.save(ContactEventJpaEntity.fromDomain(event)).toDomain();
  }

  @Override
  public Optional<ContactEvent> findById(UUID id) {
    return jpaContactEventRepository.findById(id).map(ContactEventJpaEntity::toDomain);
  }

  @Override
  public Optional<ContactEvent> findByIdWithTags(UUID id) {
    return jpaContactEventRepository.findByIdWithTags(id).map(ContactEventJpaEntity::toDomain);
  }

  @Override
  public void delete(UUID id) {
    jpaContactEventRepository.deleteById(id);
  }
}
