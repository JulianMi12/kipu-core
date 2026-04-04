package com.kipu.core.identity.infrastructure.persistence.jpa.repository;

import com.kipu.core.identity.infrastructure.persistence.jpa.entity.UserJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<UserJpaEntity, UUID> {

  Optional<UserJpaEntity> findByEmail(String email);
}
