package com.kipu.core.contacts.application.event.update;

import com.kipu.core.contacts.domain.model.enums.EventRecurrenceTypeEnum;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

public record UpdateContactEventCommand(
    UUID authenticatedUserId,
    UUID eventId,
    String title,
    String description,
    OffsetDateTime startDateTime,
    int alertLeadTimeDays,
    EventRecurrenceTypeEnum recurrenceType,
    int recurrenceInterval,
    String timezone,
    Set<UUID> tagIds) {}
