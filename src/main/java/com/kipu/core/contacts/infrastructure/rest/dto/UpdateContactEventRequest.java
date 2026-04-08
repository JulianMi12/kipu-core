package com.kipu.core.contacts.infrastructure.rest.dto;

import com.kipu.core.contacts.domain.model.enums.EventRecurrenceTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

public record UpdateContactEventRequest(
    @NotBlank String title,
    String description,
    @NotNull LocalDate baseDate,
    @PositiveOrZero int alertLeadTimeDays,
    @NotNull EventRecurrenceTypeEnum recurrenceType) {}
