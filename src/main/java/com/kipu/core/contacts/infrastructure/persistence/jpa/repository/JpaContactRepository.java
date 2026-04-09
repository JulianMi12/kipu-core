package com.kipu.core.contacts.infrastructure.persistence.jpa.repository;

import com.kipu.core.contacts.infrastructure.persistence.jpa.entity.ContactJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaContactRepository extends JpaRepository<ContactJpaEntity, UUID> {

  @Query("SELECT c FROM ContactJpaEntity c LEFT JOIN FETCH c.tagIds WHERE c.id = :id")
  Optional<ContactJpaEntity> findByIdWithTags(@Param("id") UUID id);

  Page<ContactJpaEntity> findByOwnerUserIdOrderByCreatedAtDesc(UUID ownerUserId, Pageable pageable);

  @Query(
      "SELECT c FROM ContactJpaEntity c "
          + "WHERE c.ownerUserId = :ownerUserId "
          + "AND c.id != :excludedContactId")
  Page<ContactJpaEntity> findAllByOwnerUserIdAndIdNot(
      @Param("ownerUserId") UUID ownerUserId,
      @Param("excludedContactId") UUID excludedContactId,
      Pageable pageable);
}
