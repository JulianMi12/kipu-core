package com.kipu.core.contacts.infrastructure.persistence.jpa.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.kipu.core.contacts.domain.model.UserTag;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTagJpaEntityTest {

  @Test
  @DisplayName("fromDomain: Should map Domain Model to JPA Entity correctly")
  void fromDomain_ShouldMapCorrectly() {
    // Arrange
    UUID id = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    UserTag domainTag = UserTag.reconstitute(id, ownerId, "personal", "#FFFFFF");

    // Act
    UserTagJpaEntity entity = UserTagJpaEntity.fromDomain(domainTag);

    // Assert
    assertThat(entity).isNotNull();
    assertThat(entity.getId()).isEqualTo(id);
    assertThat(entity.getOwnerUserId()).isEqualTo(ownerId);
    assertThat(entity.getName()).isEqualTo("personal");
    assertThat(entity.getColorHex()).isEqualTo("#FFFFFF");
  }

  @Test
  @DisplayName("toDomain: Should map JPA Entity back to Domain Model correctly")
  void toDomain_ShouldMapCorrectly() {
    // Arrange
    UUID id = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    UserTagJpaEntity entity = new UserTagJpaEntity(id, ownerId, "trabajo", "#000000");

    // Act
    UserTag domainTag = entity.toDomain();

    // Assert
    assertThat(domainTag).isNotNull();
    assertThat(domainTag.getId()).isEqualTo(id);
    assertThat(domainTag.getOwnerUserId()).isEqualTo(ownerId);
    assertThat(domainTag.getName()).isEqualTo("trabajo");
    assertThat(domainTag.getColorHex()).isEqualTo("#000000");
  }

  @Test
  @DisplayName("NoArgsConstructor & Setters: Should work correctly for JPA requirements")
  void noArgsConstructorAndSetters_ShouldWork() {
    // Arrange
    UserTagJpaEntity entity = new UserTagJpaEntity();
    UUID id = UUID.randomUUID();

    // Act
    entity.setId(id);
    entity.setName("urgente");
    entity.setColorHex("#FF0000");

    // Assert
    assertThat(entity.getId()).isEqualTo(id);
    assertThat(entity.getName()).isEqualTo("urgente");
    assertThat(entity.getColorHex()).isEqualTo("#FF0000");
  }

  @Test
  @DisplayName("AllArgsConstructor: Should initialize all fields correctly")
  void allArgsConstructor_ShouldWork() {
    // Arrange
    UUID id = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();

    // Act
    UserTagJpaEntity entity = new UserTagJpaEntity(id, ownerId, "test", "#123");

    // Assert
    assertThat(entity.getId()).isEqualTo(id);
    assertThat(entity.getOwnerUserId()).isEqualTo(ownerId);
    assertThat(entity.getName()).isEqualTo("test");
    assertThat(entity.getColorHex()).isEqualTo("#123");
  }
}
