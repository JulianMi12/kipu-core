package com.kipu.core.contacts.application.event.create;

import static org.assertj.core.api.Assertions.assertThat;

import com.kipu.core.contacts.domain.model.ContactEvent;
import com.kipu.core.contacts.domain.model.enums.EventRecurrenceTypeEnum;
import com.kipu.core.contacts.domain.model.enums.EventStatusEnum;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CreateContactEventResultTest {

  @Test
  void from_ShouldMapAllFieldsCorrectly_WhenDomainEventIsProvided() {
    // Arrange
    UUID eventId = UUID.randomUUID();
    UUID contactId =
        UUID.randomUUID(); // Necesario para el dominio, aunque no se expone en el Result
    String title = "Reunión de Marketing";
    String description = "Revisar KPIs del trimestre";
    LocalDate baseDate = LocalDate.of(2026, 4, 15);
    int alertLeadTimeDays = 2;
    EventRecurrenceTypeEnum recurrenceType = EventRecurrenceTypeEnum.WEEKLY;
    EventStatusEnum status = EventStatusEnum.PENDING;
    LocalDate lastCompletedDate = LocalDate.of(2026, 4, 8);
    OffsetDateTime createdAt = OffsetDateTime.now().minusDays(5);
    OffsetDateTime updatedAt = OffsetDateTime.now();

    // Reconstituimos un evento de dominio simulando que viene de la BD
    ContactEvent domainEvent =
        ContactEvent.reconstitute(
            eventId,
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
    CreateContactEventResult result = CreateContactEventResult.from(domainEvent);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(eventId);
    assertThat(result.title()).isEqualTo(title);
    assertThat(result.description()).isEqualTo(description);
    assertThat(result.baseDate()).isEqualTo(baseDate);
    assertThat(result.alertLeadTimeDays()).isEqualTo(alertLeadTimeDays);
    assertThat(result.recurrenceType()).isEqualTo(recurrenceType);
    assertThat(result.status()).isEqualTo(status);
    assertThat(result.lastCompletedDate()).isEqualTo(lastCompletedDate);
    assertThat(result.createdAt()).isEqualTo(createdAt);
    assertThat(result.updatedAt()).isEqualTo(updatedAt);
  }
}
