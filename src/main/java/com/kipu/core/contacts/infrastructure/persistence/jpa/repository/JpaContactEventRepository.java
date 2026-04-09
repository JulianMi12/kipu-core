package com.kipu.core.contacts.infrastructure.persistence.jpa.repository;

import com.kipu.core.contacts.domain.model.enums.EventStatusEnum;
import com.kipu.core.contacts.infrastructure.persistence.jpa.entity.ContactEventJpaEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
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

  @Query(
      """
    SELECT e FROM ContactEventJpaEntity e
    JOIN ContactJpaEntity c ON e.contactId = c.id
    WHERE c.ownerUserId = :ownerUserId
      AND e.status = :status
      AND e.startDateTime >= :from
    ORDER BY e.startDateTime ASC
    """)
  List<ContactEventJpaEntity> findUpcomingByOwnerUserId(
      @Param("ownerUserId") UUID ownerUserId,
      @Param("status") EventStatusEnum status,
      @Param("from") OffsetDateTime from,
      Pageable pageable);
}
