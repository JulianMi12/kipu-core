package com.kipu.core.contacts.infrastructure.persistence.jpa.repository;

import com.kipu.core.contacts.infrastructure.persistence.jpa.entity.UserTagJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JpaUserTagRepository extends JpaRepository<UserTagJpaEntity, UUID> {

  List<UserTagJpaEntity> findByOwnerUserId(UUID ownerUserId);

  boolean existsByNameAndOwnerUserId(String name, UUID ownerUserId);

  @Query(
      "SELECT t FROM UserTagJpaEntity t WHERE t.ownerUserId IN :ownerUserId AND LOWER(t.name) = LOWER(:name)")
  Optional<UserTagJpaEntity> findByOwnerUserIdAndNameIgnoreCase(UUID ownerUserId, String name);
}
