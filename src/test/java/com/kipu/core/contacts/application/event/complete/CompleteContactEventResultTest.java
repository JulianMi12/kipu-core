package com.kipu.core.contacts.application.event.complete;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.domain.model.ContactEvent;
import com.kipu.core.contacts.domain.model.enums.EventRecurrenceTypeEnum;
import com.kipu.core.contacts.domain.model.enums.EventStatusEnum;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CompleteContactEventResultTest {

  @Test
  @DisplayName("Should correctly map all fields from ContactEvent domain model")
  void shouldMapFromContactEvent() {
    // 1. Arrange (Preparar datos)
    UUID eventId = UUID.randomUUID();
    OffsetDateTime now = OffsetDateTime.now();

    // Usamos un mock para simular el comportamiento de la entidad de dominio
    ContactEvent event = mock(ContactEvent.class);
    when(event.getId()).thenReturn(eventId);
    when(event.getTitle()).thenReturn("Aniversario");
    when(event.getDescription()).thenReturn("Celebrar aniversario");
    when(event.getStartDateTime()).thenReturn(now);
    when(event.getAlertLeadTimeDays()).thenReturn(5);
    when(event.getRecurrenceType()).thenReturn(EventRecurrenceTypeEnum.YEARLY);
    when(event.getStatus()).thenReturn(EventStatusEnum.PENDING);
    when(event.getLastCompletedDate()).thenReturn(null);
    when(event.getTimezone()).thenReturn("America/Bogota");
    when(event.getCreatedAt()).thenReturn(now);
    when(event.getUpdatedAt()).thenReturn(now);

    // 2. Act (Ejecutar el método a probar)
    CompleteContactEventResult result = CompleteContactEventResult.from(event);

    // 3. Assert (Verificar resultados)
    assertAll(
        "Mapeo de campos del evento",
        () -> assertEquals(eventId, result.id()),
        () -> assertEquals("Aniversario", result.title()),
        () -> assertEquals("Celebrar aniversario", result.description()),
        () -> assertEquals(now, result.startDateTime()),
        () -> assertEquals(5, result.alertLeadTimeDays()),
        () -> assertEquals(EventRecurrenceTypeEnum.YEARLY, result.recurrenceType()),
        () -> assertEquals(EventStatusEnum.PENDING, result.status()),
        () -> assertNull(result.lastCompletedDate()),
        () -> assertEquals("America/Bogota", result.timezone()),
        () -> assertEquals(now, result.createdAt()),
        () -> assertEquals(now, result.updatedAt()));
  }
}
