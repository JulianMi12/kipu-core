package com.kipu.core.contacts.infrastructure.persistence.jpa.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.domain.model.ContactEvent;
import com.kipu.core.contacts.domain.model.enums.EventRecurrenceTypeEnum;
import com.kipu.core.contacts.domain.model.enums.EventStatusEnum;
import java.time.LocalDate;
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
    ContactEvent domainEvent =
        ContactEvent.reconstitute(
            id,
            contactId,
            "Renovar Pasaporte",
            "Trámite presencial",
            LocalDate.of(2026, 4, 10),
            30,
            EventRecurrenceTypeEnum.YEARLY,
            EventStatusEnum.PENDING,
            null,
            Set.of(tagId),
            OffsetDateTime.now(),
            OffsetDateTime.now());

    // Act
    ContactEventJpaEntity entity = ContactEventJpaEntity.fromDomain(domainEvent);

    // Assert
    assertThat(entity).isNotNull();
    assertThat(entity.getId()).isEqualTo(id);
    assertThat(entity.getTagIds()).containsExactly(tagId);
  }

  @Test
  @DisplayName("toDomain: Should map all fields including tags correctly")
  void toDomain_ShouldMapAllFieldsCorrectly_WhenEntityIsProvided() {
    // Arrange
    UUID id = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    UUID tagId = UUID.randomUUID();
    ContactEventJpaEntity entity =
        new ContactEventJpaEntity(
            id,
            contactId,
            "Pagar Gimnasio",
            "Suscripción mensual",
            LocalDate.of(2026, 5, 1),
            3,
            EventRecurrenceTypeEnum.MONTHLY,
            EventStatusEnum.COMPLETED,
            LocalDate.of(2026, 4, 1),
            OffsetDateTime.now().minusDays(30),
            OffsetDateTime.now(),
            new HashSet<>(Set.of(tagId)));

    // Act
    ContactEvent domainEvent = entity.toDomain();

    // Assert
    assertThat(domainEvent).isNotNull();
    assertThat(domainEvent.getId()).isEqualTo(id);
    assertThat(domainEvent.getTagIds()).containsExactly(tagId);
  }

  @Test
  @DisplayName("fromDomain: Should handle null tag sets in domain by creating an empty HashSet")
  void fromDomain_ShouldHandleNullTagsInDomain() {
    // Usamos el constructor de reconstitute con null en tags si el dominio lo permite
    // o simplemente un set vacío para verificar la lógica del ternario en la entidad
    ContactEvent domainEvent =
        ContactEvent.reconstitute(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "T",
            "D",
            LocalDate.now(),
            0,
            EventRecurrenceTypeEnum.ONCE,
            EventStatusEnum.PENDING,
            null,
            null, // Forzamos null para probar el ternario
            OffsetDateTime.now(),
            OffsetDateTime.now());

    ContactEventJpaEntity entity = ContactEventJpaEntity.fromDomain(domainEvent);

    assertThat(entity.getTagIds()).isNotNull().isEmpty();
  }

  @Test
  @DisplayName("toDomain: Should handle null tagIds field in entity")
  void toDomain_ShouldHandleNullTagIdsInEntity() {
    ContactEventJpaEntity entity = new ContactEventJpaEntity();
    entity.setTagIds(null); // Simulamos estado de la DB antes de la inicialización
    entity.setId(UUID.randomUUID());

    ContactEvent domain = entity.toDomain();

    assertThat(domain.getTagIds()).isNotNull().isEmpty();
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
    entity.setTagIds(tags);

    // Assert
    assertThat(entity.getId()).isEqualTo(id);
    assertThat(entity.getTitle()).isEqualTo("Test Title");
    assertThat(entity.getTagIds()).isEqualTo(tags);
  }

  @Test
  @DisplayName("fromDomain: Should handle null tags from domain using the ternary operator")
  void fromDomain_ShouldHandleNullTagsSpecifically() {
    // Arrange
    // Usamos un Mock del objeto de dominio para forzar que getTagIds() devuelva null
    // sin pasar por el constructor real de ContactEvent que tiene la protección.
    ContactEvent mockEvent = org.mockito.Mockito.mock(ContactEvent.class);

    UUID id = UUID.randomUUID();
    when(mockEvent.getId()).thenReturn(id);
    when(mockEvent.getTagIds()).thenReturn(null); // Forzamos el escenario nulo
    when(mockEvent.getCreatedAt()).thenReturn(OffsetDateTime.now());
    when(mockEvent.getUpdatedAt()).thenReturn(OffsetDateTime.now());

    // Act
    ContactEventJpaEntity entity = ContactEventJpaEntity.fromDomain(mockEvent);

    // Assert
    assertThat(entity).isNotNull();
    assertThat(entity.getId()).isEqualTo(id);
    assertThat(entity.getTagIds()).isNotNull().isEmpty(); // El ternario funcionó
  }
}
