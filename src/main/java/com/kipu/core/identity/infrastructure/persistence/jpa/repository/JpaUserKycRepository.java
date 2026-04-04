package com.kipu.core.identity.infrastructure.persistence.jpa.repository;

import com.kipu.core.identity.infrastructure.persistence.jpa.entity.UserKycJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserKycRepository extends JpaRepository<UserKycJpaEntity, UUID> {}
