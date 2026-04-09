package com.kipu.core.contacts.infrastructure.persistence.jpa.repository;

import com.kipu.core.contacts.infrastructure.persistence.jpa.entity.ContactEventJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaContactEventRepository extends JpaRepository<ContactEventJpaEntity, UUID> {

  @Query("SELECT e FROM ContactEventJpaEntity e LEFT JOIN FETCH e.tagIds WHERE e.id = :id")
  Optional<ContactEventJpaEntity> findByIdWithTags(@Param("id") UUID id);

  @Query(
      "SELECT e FROM ContactEventJpaEntity e JOIN e.tagIds t WHERE e.contactId = :contactId AND t = :tagId")
  Optional<ContactEventJpaEntity> findByContactIdAndTagId(
      @Param("contactId") UUID contactId, @Param("tagId") UUID tagId);
}
