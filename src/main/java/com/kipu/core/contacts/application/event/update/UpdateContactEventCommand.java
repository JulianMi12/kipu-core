package com.kipu.core.contacts.application.event.update;

import com.kipu.core.contacts.domain.model.enums.EventRecurrenceTypeEnum;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateContactEventCommand(
    UUID authenticatedUserId,
    UUID eventId,
    String title,
    String description,
    LocalDate baseDate,
    int alertLeadTimeDays,
    EventRecurrenceTypeEnum recurrenceType) {}
