package com.kipu.core.identity.infrastructure.persistence.jpa.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.kipu.core.identity.domain.model.User;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserJpaEntityTest {

  @Test
  @DisplayName("fromDomain: Should map all fields from Domain to JPA Entity")
  void fromDomain_ShouldMapCorrectly() {
    // Arrange
    User user = User.create("test@kipu.com", "hash123");

    // Act
    UserJpaEntity jpaEntity = UserJpaEntity.fromDomain(user);

    // Assert
    assertThat(jpaEntity.getId()).isEqualTo(user.getId());
    assertThat(jpaEntity.getEmail()).isEqualTo(user.getEmail());
    assertThat(jpaEntity.getPasswordHash()).isEqualTo(user.getPasswordHash());
    assertThat(jpaEntity.isActive()).isTrue();
    assertThat(jpaEntity.getCreatedAt()).isEqualTo(user.getCreatedAt());
  }

  @Test
  @DisplayName("toDomain: Should reconstitute Domain model from JPA Entity")
  void toDomain_ShouldMapCorrectly() {
    // Arrange
    UUID id = UUID.randomUUID();
    OffsetDateTime now = OffsetDateTime.now();
    UserJpaEntity jpaEntity = new UserJpaEntity(id, "persisted@kipu.com", "saved-hash", false, now);

    // Act
    User user = jpaEntity.toDomain();

    // Assert
    assertThat(user.getId()).isEqualTo(id);
    assertThat(user.getEmail()).isEqualTo("persisted@kipu.com");
    assertThat(user.getPasswordHash()).isEqualTo("saved-hash");
    assertThat(user.isActive()).isFalse();
    assertThat(user.getCreatedAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("NoArgsConstructor & Setters: Should work for JPA requirements")
  void jpaRequirements_ShouldBeMet() {
    UserJpaEntity entity = new UserJpaEntity();
    UUID id = UUID.randomUUID();

    entity.setId(id);
    entity.setEmail("jpa@kipu.com");

    assertThat(entity.getId()).isEqualTo(id);
    assertThat(entity.getEmail()).isEqualTo("jpa@kipu.com");
  }
}
