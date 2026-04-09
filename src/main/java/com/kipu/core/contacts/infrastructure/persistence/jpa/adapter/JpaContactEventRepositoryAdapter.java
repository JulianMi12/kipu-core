package com.kipu.core.contacts.infrastructure.persistence.jpa.adapter;

import com.kipu.core.contacts.domain.model.ContactEvent;
import com.kipu.core.contacts.domain.model.enums.EventStatusEnum;
import com.kipu.core.contacts.domain.repository.ContactEventRepository;
import com.kipu.core.contacts.infrastructure.persistence.jpa.entity.ContactEventJpaEntity;
import com.kipu.core.contacts.infrastructure.persistence.jpa.repository.JpaContactEventRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

  @Override
  public Optional<ContactEvent> findByContactIdAndTagIdsContains(UUID contactId, UUID tagId) {
    return jpaContactEventRepository
        .findByContactIdAndTagId(contactId, tagId)
        .map(ContactEventJpaEntity::toDomain);
  }

  @Override
  public List<ContactEvent> findUpcomingByOwnerUserId(
      UUID ownerUserId, OffsetDateTime from, int limit) {
    Pageable pageable = PageRequest.of(0, limit, Sort.by("startDateTime").ascending());
    return jpaContactEventRepository
        .findUpcomingByOwnerUserId(ownerUserId, EventStatusEnum.PENDING, from, pageable)
        .stream()
        .map(ContactEventJpaEntity::toDomain)
        .toList();
  }
}
