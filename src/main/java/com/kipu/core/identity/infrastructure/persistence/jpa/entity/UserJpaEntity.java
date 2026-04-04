package com.kipu.core.identity.infrastructure.persistence.jpa.entity;

import com.kipu.core.identity.domain.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users", schema = "identity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserJpaEntity {

  @Id
  @Column(nullable = false, updatable = false)
  private UUID id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Column(name = "is_active", nullable = false)
  private boolean active;

  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  public static UserJpaEntity fromDomain(User user) {
    return new UserJpaEntity(
        user.getId(),
        user.getEmail(),
        user.getPasswordHash(),
        user.isActive(),
        user.getCreatedAt());
  }

  public User toDomain() {
    return User.reconstitute(id, email, passwordHash, active, createdAt);
  }
}
