package com.kipu.core.contacts.application.event.create;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.domain.model.ContactEvent;
import com.kipu.core.contacts.domain.model.enums.EventRecurrenceTypeEnum;
import com.kipu.core.contacts.domain.model.enums.EventStatusEnum;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreateContactEventResultTest {

  @Test
  @DisplayName("from: Should map all fields correctly from ContactEvent domain model")
  void from_ShouldMapAllFieldsCorrectly_WhenDomainEventIsProvided() {
    // Arrange
    UUID eventId = UUID.randomUUID();
    String title = "Cumpleaños de Julian 🎂";
    String description = "¡Hoy celebramos tu vida! Disfruta de tu día al máximo.";
    OffsetDateTime startDateTime = OffsetDateTime.now().plusDays(10);
    int alertLeadTimeDays = 0;
    EventRecurrenceTypeEnum recurrenceType = EventRecurrenceTypeEnum.YEARLY;
    EventStatusEnum status = EventStatusEnum.PENDING;
    OffsetDateTime lastCompletedDate = null;
    String timezone = "America/Bogota";
    Set<UUID> tagIds = Set.of(UUID.randomUUID());

    // Mockeamos el dominio para evitar dependencias de lógica interna del modelo
    ContactEvent domainEvent = mock(ContactEvent.class);
    when(domainEvent.getId()).thenReturn(eventId);
    when(domainEvent.getTitle()).thenReturn(title);
    when(domainEvent.getDescription()).thenReturn(description);
    when(domainEvent.getStartDateTime()).thenReturn(startDateTime);
    when(domainEvent.getAlertLeadTimeDays()).thenReturn(alertLeadTimeDays);
    when(domainEvent.getRecurrenceType()).thenReturn(recurrenceType);
    when(domainEvent.getStatus()).thenReturn(status);
    when(domainEvent.getLastCompletedDate()).thenReturn(lastCompletedDate);
    when(domainEvent.getTimezone()).thenReturn(timezone);
    when(domainEvent.getTagIds()).thenReturn(tagIds);

    // Act
    CreateContactEventResult result = CreateContactEventResult.from(domainEvent);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(eventId);
    assertThat(result.title()).isEqualTo(title);
    assertThat(result.description()).isEqualTo(description);
    assertThat(result.startDateTime()).isEqualTo(startDateTime);
    assertThat(result.alertLeadTimeDays()).isEqualTo(alertLeadTimeDays);
    assertThat(result.recurrenceType()).isEqualTo(recurrenceType);
    assertThat(result.status()).isEqualTo(status);
    assertThat(result.lastCompletedDate()).isEqualTo(lastCompletedDate);
    assertThat(result.timezone()).isEqualTo(timezone);
    assertThat(result.tagIds()).containsExactlyInAnyOrderElementsOf(tagIds);
  }
}
