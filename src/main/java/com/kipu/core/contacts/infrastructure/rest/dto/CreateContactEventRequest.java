package com.kipu.core.contacts.infrastructure.rest.dto;

import com.kipu.core.contacts.domain.model.enums.EventRecurrenceTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record CreateContactEventRequest(
    @NotBlank String title,
    String description,
    @NotNull LocalDate baseDate,
    @PositiveOrZero int alertLeadTimeDays,
    @NotNull EventRecurrenceTypeEnum recurrenceType,
    Set<UUID> tagIds) {}
