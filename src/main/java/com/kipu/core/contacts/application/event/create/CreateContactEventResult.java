package com.kipu.core.contacts.application.event.create;

import com.kipu.core.contacts.domain.model.ContactEvent;
import com.kipu.core.contacts.domain.model.enums.EventRecurrenceTypeEnum;
import com.kipu.core.contacts.domain.model.enums.EventStatusEnum;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record CreateContactEventResult(
    UUID id,
    String title,
    String description,
    LocalDate baseDate,
    int alertLeadTimeDays,
    EventRecurrenceTypeEnum recurrenceType,
    EventStatusEnum status,
    LocalDate lastCompletedDate,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {

  public static CreateContactEventResult from(ContactEvent event) {
    return new CreateContactEventResult(
        event.getId(),
        event.getTitle(),
        event.getDescription(),
        event.getBaseDate(),
        event.getAlertLeadTimeDays(),
        event.getRecurrenceType(),
        event.getStatus(),
        event.getLastCompletedDate(),
        event.getCreatedAt(),
        event.getUpdatedAt());
  }
}
