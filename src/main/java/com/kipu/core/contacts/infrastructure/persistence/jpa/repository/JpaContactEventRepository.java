package com.kipu.core.contacts.infrastructure.persistence.jpa.repository;

import com.kipu.core.contacts.infrastructure.persistence.jpa.entity.ContactEventJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaContactEventRepository extends JpaRepository<ContactEventJpaEntity, UUID> {}
