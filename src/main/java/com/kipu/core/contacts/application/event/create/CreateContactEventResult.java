package com.kipu.core.contacts.application.event.create;

import com.kipu.core.contacts.domain.model.ContactEvent;
import com.kipu.core.contacts.domain.model.enums.EventRecurrenceTypeEnum;
import com.kipu.core.contacts.domain.model.enums.EventStatusEnum;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

public record CreateContactEventResult(
    UUID id,
    String title,
    String description,
    OffsetDateTime startDateTime,
    int alertLeadTimeDays,
    EventRecurrenceTypeEnum recurrenceType,
    EventStatusEnum status,
    OffsetDateTime lastCompletedDate,
    String timezone,
    Set<UUID> tagIds) {

  public static CreateContactEventResult from(ContactEvent event) {
    return new CreateContactEventResult(
        event.getId(),
        event.getTitle(),
        event.getDescription(),
        event.getStartDateTime(),
        event.getAlertLeadTimeDays(),
        event.getRecurrenceType(),
        event.getStatus(),
        event.getLastCompletedDate(),
        event.getTimezone(),
        event.getTagIds());
  }
}
