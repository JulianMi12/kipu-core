package com.kipu.core.contacts.infrastructure.persistence.jpa.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.domain.model.Contact;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContactJpaEntityTest {

  @Test
  @DisplayName("fromDomain: Should map Domain Model to JPA Entity correctly including tags")
  void fromDomain_ShouldMapCorrectly() {
    // Arrange
    UUID id = UUID.randomUUID();
    UUID tagId = UUID.randomUUID();
    Contact contact = mock(Contact.class);

    when(contact.getId()).thenReturn(id);
    when(contact.getFirstName()).thenReturn("Julian");
    when(contact.getTagIds()).thenReturn(Set.of(tagId));
    when(contact.getCreatedAt()).thenReturn(OffsetDateTime.now());

    // Act
    ContactJpaEntity jpaEntity = ContactJpaEntity.fromDomain(contact);

    // Assert
    assertNotNull(jpaEntity);
    assertEquals(id, jpaEntity.getId());
    assertTrue(jpaEntity.getTagIds().contains(tagId));
  }

  @Test
  @DisplayName("fromDomain: Should handle null tag sets in domain via ternary operator")
  void fromDomain_ShouldHandleNullTags() {
    // Arrange
    Contact contact = mock(Contact.class);
    when(contact.getTagIds()).thenReturn(null); // Forzamos el nulo
    when(contact.getCreatedAt()).thenReturn(OffsetDateTime.now());

    // Act
    ContactJpaEntity jpaEntity = ContactJpaEntity.fromDomain(contact);

    // Assert
    assertNotNull(jpaEntity.getTagIds());
    assertTrue(jpaEntity.getTagIds().isEmpty());
  }

  @Test
  @DisplayName("toDomain: Should map JPA Entity back to Domain Model correctly including tags")
  void toDomain_ShouldMapCorrectly() {
    // Arrange
    UUID id = UUID.randomUUID();
    UUID tagId = UUID.randomUUID();
    ContactJpaEntity jpaEntity = new ContactJpaEntity();
    jpaEntity.setId(id);
    jpaEntity.setFirstName("Julian");
    jpaEntity.setTagIds(new HashSet<>(Set.of(tagId)));
    jpaEntity.setCreatedAt(OffsetDateTime.now());

    // Act
    Contact contact = jpaEntity.toDomain();

    // Assert
    assertNotNull(contact);
    assertEquals(id, contact.getId());
    assertTrue(contact.getTagIds().contains(tagId));
  }

  @Test
  @DisplayName("toDomain: Should handle null tagIds in entity via ternary operator")
  void toDomain_ShouldHandleNullTagIds() {
    // Arrange
    ContactJpaEntity jpaEntity = new ContactJpaEntity();
    jpaEntity.setTagIds(null); // Simulamos estado nulo de Hibernate
    jpaEntity.setCreatedAt(OffsetDateTime.now());

    // Act
    Contact contact = jpaEntity.toDomain();

    // Assert
    assertNotNull(contact.getTagIds());
    assertTrue(contact.getTagIds().isEmpty());
  }

  @Test
  @DisplayName("Setters and Getters: Should work correctly for all fields")
  void settersAndGetters_ShouldWork() {
    // Arrange
    ContactJpaEntity entity = new ContactJpaEntity();
    UUID id = UUID.randomUUID();
    Map<String, Object> attrs = Map.of("test", "value");

    // Act
    entity.setId(id);
    entity.setLastName("Miranda");
    entity.setDynamicAttributes(attrs);

    // Assert
    assertEquals(id, entity.getId());
    assertEquals("Miranda", entity.getLastName());
    assertEquals(attrs, entity.getDynamicAttributes());
  }

  @Test
  @DisplayName("Constructor: Should support NoArgsConstructor for Hibernate")
  void noArgsConstructor_ShouldWork() {
    // Act
    ContactJpaEntity jpaEntity = new ContactJpaEntity();

    // Assert
    assertNotNull(jpaEntity);
  }
}
