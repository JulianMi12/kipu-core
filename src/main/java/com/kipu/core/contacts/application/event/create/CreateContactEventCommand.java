package com.kipu.core.contacts.application.event.create;

import com.kipu.core.contacts.domain.model.enums.EventRecurrenceTypeEnum;
import java.time.LocalDate;
import java.util.UUID;

public record CreateContactEventCommand(
    UUID authenticatedUserId,
    UUID contactId,
    String title,
    String description,
    LocalDate baseDate,
    int alertLeadTimeDays,
    EventRecurrenceTypeEnum recurrenceType) {}
