package com.kipu.core.contacts.infrastructure.persistence.jpa.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.kipu.core.contacts.domain.model.ContactEvent;
import com.kipu.core.contacts.domain.model.enums.EventRecurrenceTypeEnum;
import com.kipu.core.contacts.domain.model.enums.EventStatusEnum;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ContactEventJpaEntityTest {

  @Test
  void fromDomain_ShouldMapAllFieldsCorrectly_WhenDomainEventIsProvided() {
    // Arrange
    UUID id = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    String title = "Renovar Pasaporte";
    String description = "Trámite presencial";
    LocalDate baseDate = LocalDate.of(2026, 4, 10);
    int alertLeadTimeDays = 30;
    EventRecurrenceTypeEnum recurrenceType = EventRecurrenceTypeEnum.YEARLY;
    EventStatusEnum status = EventStatusEnum.PENDING;
    LocalDate lastCompletedDate = LocalDate.of(2025, 4, 10);
    OffsetDateTime createdAt = OffsetDateTime.now().minusDays(1);
    OffsetDateTime updatedAt = OffsetDateTime.now();

    ContactEvent domainEvent =
        ContactEvent.reconstitute(
            id,
            contactId,
            title,
            description,
            baseDate,
            alertLeadTimeDays,
            recurrenceType,
            status,
            lastCompletedDate,
            createdAt,
            updatedAt);

    // Act
    ContactEventJpaEntity entity = ContactEventJpaEntity.fromDomain(domainEvent);

    // Assert
    assertThat(entity).isNotNull();
    assertThat(entity.getId()).isEqualTo(id);
    assertThat(entity.getContactId()).isEqualTo(contactId);
    assertThat(entity.getTitle()).isEqualTo(title);
    assertThat(entity.getDescription()).isEqualTo(description);
    assertThat(entity.getBaseDate()).isEqualTo(baseDate);
    assertThat(entity.getAlertLeadTimeDays()).isEqualTo(alertLeadTimeDays);
    assertThat(entity.getRecurrenceType()).isEqualTo(recurrenceType);
    assertThat(entity.getStatus()).isEqualTo(status);
    assertThat(entity.getLastCompletedDate()).isEqualTo(lastCompletedDate);
    assertThat(entity.getCreatedAt()).isEqualTo(createdAt);
    assertThat(entity.getUpdatedAt()).isEqualTo(updatedAt);
  }

  @Test
  void toDomain_ShouldMapAllFieldsCorrectly_WhenEntityIsProvided() {
    // Arrange
    UUID id = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    String title = "Pagar Gimnasio";
    String description = "Suscripción mensual";
    LocalDate baseDate = LocalDate.of(2026, 5, 1);
    int alertLeadTimeDays = 3;
    EventRecurrenceTypeEnum recurrenceType = EventRecurrenceTypeEnum.MONTHLY;
    EventStatusEnum status = EventStatusEnum.COMPLETED;
    LocalDate lastCompletedDate = LocalDate.of(2026, 4, 1);
    OffsetDateTime createdAt = OffsetDateTime.now().minusDays(30);
    OffsetDateTime updatedAt = OffsetDateTime.now();

    ContactEventJpaEntity entity =
        new ContactEventJpaEntity(
            id,
            contactId,
            title,
            description,
            baseDate,
            alertLeadTimeDays,
            recurrenceType,
            status,
            lastCompletedDate,
            createdAt,
            updatedAt);

    // Act
    ContactEvent domainEvent = entity.toDomain();

    // Assert
    assertThat(domainEvent).isNotNull();
    assertThat(domainEvent.getId()).isEqualTo(id);
    assertThat(domainEvent.getContactId()).isEqualTo(contactId);
    assertThat(domainEvent.getTitle()).isEqualTo(title);
    assertThat(domainEvent.getDescription()).isEqualTo(description);
    assertThat(domainEvent.getBaseDate()).isEqualTo(baseDate);
    assertThat(domainEvent.getAlertLeadTimeDays()).isEqualTo(alertLeadTimeDays);
    assertThat(domainEvent.getRecurrenceType()).isEqualTo(recurrenceType);
    assertThat(domainEvent.getStatus()).isEqualTo(status);
    assertThat(domainEvent.getLastCompletedDate()).isEqualTo(lastCompletedDate);
    assertThat(domainEvent.getCreatedAt()).isEqualTo(createdAt);
    assertThat(domainEvent.getUpdatedAt()).isEqualTo(updatedAt);
  }

  @Test
  void constructorAndSetters_ShouldWorkCorrectly_ForJpaFramework() {
    // Arrange
    ContactEventJpaEntity entity = new ContactEventJpaEntity();
    UUID id = UUID.randomUUID();

    // Act
    entity.setId(id);
    entity.setTitle("Test Title");

    // Assert
    assertThat(entity.getId()).isEqualTo(id);
    assertThat(entity.getTitle()).isEqualTo("Test Title");
  }
}
