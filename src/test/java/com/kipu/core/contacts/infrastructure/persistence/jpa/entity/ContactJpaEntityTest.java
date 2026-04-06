package com.kipu.core.contacts.infrastructure.persistence.jpa.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.domain.model.Contact;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContactJpaEntityTest {

  @Test
  @DisplayName("fromDomain: Should map Domain Model to JPA Entity correctly")
  void fromDomain_ShouldMapCorrectively() {
    // Arrange
    UUID id = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    OffsetDateTime now = OffsetDateTime.now();
    LocalDate birthdate = LocalDate.of(1995, 5, 20);
    Map<String, Object> attrs = Map.of("source", "onboarding");

    Contact contact = mock(Contact.class);
    when(contact.getId()).thenReturn(id);
    when(contact.getOwnerUserId()).thenReturn(ownerId);
    when(contact.getFirstName()).thenReturn("Julian");
    when(contact.getLastName()).thenReturn("Miranda");
    when(contact.getPrimaryEmail()).thenReturn("dev@kipu.com");
    when(contact.getBirthdate()).thenReturn(birthdate);
    when(contact.getDynamicAttributes()).thenReturn(attrs);
    when(contact.getCreatedAt()).thenReturn(now);

    // Act
    ContactJpaEntity jpaEntity = ContactJpaEntity.fromDomain(contact);

    // Assert
    assertNotNull(jpaEntity);
    assertEquals(id, jpaEntity.getId());
    assertEquals(ownerId, jpaEntity.getOwnerUserId());
    assertEquals("Julian", jpaEntity.getFirstName());
    assertEquals("Miranda", jpaEntity.getLastName());
    assertEquals("dev@kipu.com", jpaEntity.getPrimaryEmail());
    assertEquals(birthdate, jpaEntity.getBirthdate());
    assertEquals(attrs, jpaEntity.getDynamicAttributes());
    assertEquals(now, jpaEntity.getCreatedAt());
  }

  @Test
  @DisplayName("toDomain: Should map JPA Entity back to Domain Model correctly")
  void toDomain_ShouldMapCorrectively() {
    // Arrange
    UUID id = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    OffsetDateTime now = OffsetDateTime.now();
    LocalDate birthdate = LocalDate.of(1990, 1, 1);
    Map<String, Object> attrs = Map.of("sync", true);

    ContactJpaEntity jpaEntity =
        new ContactJpaEntity(
            id, ownerId, "Julian", "Miranda", "dev@kipu.com", birthdate, attrs, now);

    // Act
    Contact contact = jpaEntity.toDomain();

    // Assert
    assertNotNull(contact);
    assertEquals(id, contact.getId());
    assertEquals(ownerId, contact.getOwnerUserId());
    assertEquals("Julian", contact.getFirstName());
    assertEquals("Miranda", contact.getLastName());
    assertEquals("dev@kipu.com", contact.getPrimaryEmail());
    assertEquals(birthdate, contact.getBirthdate());
    assertEquals(attrs, contact.getDynamicAttributes());
    assertEquals(now, contact.getCreatedAt());
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
