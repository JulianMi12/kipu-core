package com.kipu.core.contacts.infrastructure.persistence.jpa.adapter;

import com.kipu.core.contacts.domain.model.UserTag;
import com.kipu.core.contacts.domain.repository.UserTagRepository;
import com.kipu.core.contacts.infrastructure.persistence.jpa.entity.UserTagJpaEntity;
import com.kipu.core.contacts.infrastructure.persistence.jpa.repository.JpaUserTagRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaUserTagRepositoryAdapter implements UserTagRepository {

  private final JpaUserTagRepository jpaUserTagRepository;

  @Override
  public UserTag save(UserTag tag) {
    return jpaUserTagRepository.save(UserTagJpaEntity.fromDomain(tag)).toDomain();
  }

  @Override
  public Optional<UserTag> findById(UUID id) {
    return jpaUserTagRepository.findById(id).map(UserTagJpaEntity::toDomain);
  }

  @Override
  public List<UserTag> findByOwnerUserId(UUID ownerUserId) {
    return jpaUserTagRepository.findByOwnerUserId(ownerUserId).stream()
        .map(UserTagJpaEntity::toDomain)
        .toList();
  }

  @Override
  public void delete(UUID id) {
    jpaUserTagRepository.deleteById(id);
  }

  @Override
  public boolean existsByNameAndOwnerUserId(String name, UUID ownerUserId) {
    return jpaUserTagRepository.existsByNameAndOwnerUserId(name, ownerUserId);
  }

  @Override
  public Optional<UserTag> findByOwnerUserIdAndNameIgnoreCase(UUID ownerUserId, String name) {
    return jpaUserTagRepository
        .findByOwnerUserIdAndNameIgnoreCase(ownerUserId, name)
        .map(UserTagJpaEntity::toDomain);
  }

  @Override
  public List<UserTag> findAllById(Iterable<UUID> ids) {
    return jpaUserTagRepository.findAllById(ids).stream().map(UserTagJpaEntity::toDomain).toList();
  }
}
