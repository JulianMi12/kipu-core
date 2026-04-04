package com.kipu.core.identity.infrastructure.persistence.jpa.repository;

import com.kipu.core.identity.infrastructure.persistence.jpa.entity.RefreshTokenJpaEntity;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaRefreshTokenRepository extends JpaRepository<RefreshTokenJpaEntity, UUID> {

  void deleteByUserId(UUID userId);

  @Modifying
  @Query("UPDATE RefreshTokenJpaEntity r SET r.revoked = true WHERE r.token = :token")
  void revokeByToken(@Param("token") String token);

  @Query(
      "SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END"
          + " FROM RefreshTokenJpaEntity r"
          + " WHERE r.token = :token AND r.revoked = false AND r.expiryDate > :now")
  boolean existsByTokenAndRevokedFalseAndExpiryDateAfter(
      @Param("token") String token, @Param("now") OffsetDateTime now);
}
