package com.kipu.core.contacts.infrastructure.persistence.jpa.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.domain.model.ContactEvent;
import com.kipu.core.contacts.domain.model.enums.EventRecurrenceTypeEnum;
import com.kipu.core.contacts.domain.model.enums.EventStatusEnum;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContactEventJpaEntityTest {

  @Test
  @DisplayName("fromDomain: Should map all fields including tags correctly")
  void fromDomain_ShouldMapAllFieldsCorrectly_WhenDomainEventIsProvided() {
    // Arrange
    UUID id = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    UUID tagId = UUID.randomUUID();
    OffsetDateTime startDateTime = OffsetDateTime.now().plusDays(5);

    // Reconstitute con 12 argumentos
    ContactEvent domainEvent =
        ContactEvent.reconstitute(
            id,
            contactId,
            "Renovar Pasaporte",
            "Trámite presencial",
            startDateTime,
            30,
            EventRecurrenceTypeEnum.YEARLY,
            1, // recurrenceInterval
            EventStatusEnum.PENDING,
            null,
            "UTC",
            Set.of(tagId),
            OffsetDateTime.now(),
            OffsetDateTime.now());

    // Act
    ContactEventJpaEntity entity = ContactEventJpaEntity.fromDomain(domainEvent);

    // Assert
    assertThat(entity).isNotNull();
    assertThat(entity.getId()).isEqualTo(id);
    assertThat(entity.getRecurrenceInterval()).isEqualTo(1);
    assertThat(entity.getTimezone()).isEqualTo("UTC");
    assertThat(entity.getTagIds()).containsExactly(tagId);
  }

  @Test
  @DisplayName("toDomain: Should map all fields including tags correctly")
  void toDomain_ShouldMapAllFieldsCorrectly_WhenEntityIsProvided() {
    // Arrange
    UUID id = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    UUID tagId = UUID.randomUUID();
    OffsetDateTime startDateTime = OffsetDateTime.now();

    ContactEventJpaEntity entity =
        new ContactEventJpaEntity(
            id,
            contactId,
            "Pagar Gimnasio",
            "Suscripción mensual",
            startDateTime,
            3,
            EventRecurrenceTypeEnum.MONTHLY,
            1, // recurrenceInterval
            EventStatusEnum.COMPLETED,
            startDateTime.minusMonths(1),
            "America/Bogota",
            OffsetDateTime.now().minusDays(30),
            OffsetDateTime.now(),
            new HashSet<>(Set.of(tagId)));

    // Act
    ContactEvent domainEvent = entity.toDomain();

    // Assert
    assertThat(domainEvent).isNotNull();
    assertThat(domainEvent.getId()).isEqualTo(id);
    assertThat(domainEvent.getTagIds()).containsExactly(tagId);
    assertThat(domainEvent.getTimezone()).isEqualTo("America/Bogota");
  }

  @Test
  @DisplayName("fromDomain: Should handle null tag sets in domain by creating an empty HashSet")
  void fromDomain_ShouldHandleNullTagsInDomain() {
    ContactEvent domainEvent =
        ContactEvent.reconstitute(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "T",
            "D",
            OffsetDateTime.now(),
            0,
            EventRecurrenceTypeEnum.ONCE,
            1,
            EventStatusEnum.PENDING,
            null,
            "UTC",
            null, // Forzamos null para probar el mapeo
            OffsetDateTime.now(),
            OffsetDateTime.now());

    ContactEventJpaEntity entity = ContactEventJpaEntity.fromDomain(domainEvent);

    assertThat(entity.getTagIds()).isNotNull().isEmpty();
  }

  @Test
  @DisplayName("toDomain: Should handle null tagIds field in entity")
  void toDomain_ShouldHandleNullTagIdsInEntity() {
    ContactEventJpaEntity entity = new ContactEventJpaEntity();
    entity.setTagIds(null);
    entity.setId(UUID.randomUUID());
    entity.setStartDateTime(OffsetDateTime.now()); // Requerido para evitar NPE en toDomain
    entity.setRecurrenceType(EventRecurrenceTypeEnum.ONCE);

    ContactEvent domain = entity.toDomain();

    assertThat(domain.getTagIds()).isNotNull().isEmpty();
  }

  @Test
  @DisplayName("fromDomain: Should handle null tags from domain using the ternary operator")
  void fromDomain_ShouldHandleNullTagsSpecifically() {
    // Arrange
    ContactEvent mockEvent = org.mockito.Mockito.mock(ContactEvent.class);

    UUID id = UUID.randomUUID();
    when(mockEvent.getId()).thenReturn(id);
    when(mockEvent.getTagIds()).thenReturn(null);
    when(mockEvent.getCreatedAt()).thenReturn(OffsetDateTime.now());
    when(mockEvent.getUpdatedAt()).thenReturn(OffsetDateTime.now());

    // Act
    ContactEventJpaEntity entity = ContactEventJpaEntity.fromDomain(mockEvent);

    // Assert
    assertThat(entity).isNotNull();
    assertThat(entity.getTagIds()).isNotNull().isEmpty();
  }

  @Test
  @DisplayName("constructorAndSetters: Should work correctly for JPA framework")
  void constructorAndSetters_ShouldWorkCorrectly_ForJpaFramework() {
    // Arrange
    ContactEventJpaEntity entity = new ContactEventJpaEntity();
    UUID id = UUID.randomUUID();
    Set<UUID> tags = new HashSet<>(Set.of(UUID.randomUUID()));

    // Act
    entity.setId(id);
    entity.setTitle("Test Title");
    entity.setRecurrenceInterval(1);
    entity.setTimezone("UTC");
    entity.setTagIds(tags);

    // Assert
    assertThat(entity.getId()).isEqualTo(id);
    assertThat(entity.getTitle()).isEqualTo("Test Title");
    assertThat(entity.getTagIds()).isEqualTo(tags);
    assertThat(entity.getRecurrenceInterval()).isEqualTo(1);
  }
}
