package com.kipu.core.contacts.application.event.create;

import com.kipu.core.contacts.domain.model.enums.EventRecurrenceTypeEnum;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

public record CreateContactEventCommand(
    UUID authenticatedUserId,
    UUID contactId,
    String title,
    String description,
    OffsetDateTime startDateTime,
    int alertLeadTimeDays,
    EventRecurrenceTypeEnum recurrenceType,
    int recurrenceInterval,
    String timezone,
    Set<UUID> tagIds) {}
