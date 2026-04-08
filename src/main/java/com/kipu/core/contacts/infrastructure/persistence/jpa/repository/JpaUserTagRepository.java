package com.kipu.core.contacts.infrastructure.persistence.jpa.repository;

import com.kipu.core.contacts.infrastructure.persistence.jpa.entity.UserTagJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserTagRepository extends JpaRepository<UserTagJpaEntity, UUID> {

  List<UserTagJpaEntity> findByOwnerUserId(UUID ownerUserId);

  boolean existsByNameAndOwnerUserId(String name, UUID ownerUserId);
}
