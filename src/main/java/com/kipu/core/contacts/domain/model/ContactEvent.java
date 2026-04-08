package com.kipu.core.contacts.domain.model;

import com.kipu.core.contacts.domain.model.enums.EventRecurrenceTypeEnum;
import com.kipu.core.contacts.domain.model.enums.EventStatusEnum;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ContactEvent {

  private final UUID id;
  private final UUID contactId;
  private String title;
  private String description;
  private LocalDate baseDate;
  private int alertLeadTimeDays;
  private EventRecurrenceTypeEnum recurrenceType;
  private EventStatusEnum status;
  private LocalDate lastCompletedDate;
  private final OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;

  public static ContactEvent create(
      UUID contactId,
      String title,
      String description,
      LocalDate baseDate,
      int alertLeadTimeDays,
      EventRecurrenceTypeEnum recurrenceType) {
    OffsetDateTime now = OffsetDateTime.now();
    return new ContactEvent(
        UUID.randomUUID(),
        contactId,
        title,
        description,
        baseDate,
        alertLeadTimeDays,
        recurrenceType,
        EventStatusEnum.PENDING,
        null,
        now,
        now);
  }

  public static ContactEvent reconstitute(
      UUID id,
      UUID contactId,
      String title,
      String description,
      LocalDate baseDate,
      int alertLeadTimeDays,
      EventRecurrenceTypeEnum recurrenceType,
      EventStatusEnum status,
      LocalDate lastCompletedDate,
      OffsetDateTime createdAt,
      OffsetDateTime updatedAt) {
    return new ContactEvent(
        id,
        contactId,
        title,
        description,
        baseDate,
        alertLeadTimeDays,
        recurrenceType,
        status,
        lastCompletedDate,
        createdAt,
        updatedAt);
  }

  public void update(
      String title,
      String description,
      LocalDate baseDate,
      int alertLeadTimeDays,
      EventRecurrenceTypeEnum recurrenceType) {
    this.title = title;
    this.description = description;
    this.baseDate = baseDate;
    this.alertLeadTimeDays = alertLeadTimeDays;
    this.recurrenceType = recurrenceType;
    this.updatedAt = OffsetDateTime.now();
  }

  public void complete() {
    this.lastCompletedDate = LocalDate.now();
    switch (this.recurrenceType) {
      case ONCE -> this.status = EventStatusEnum.COMPLETED;
      case DAILY -> this.baseDate = this.baseDate.plusDays(1);
      case WEEKLY -> this.baseDate = this.baseDate.plusWeeks(1);
      case MONTHLY -> this.baseDate = this.baseDate.plusMonths(1);
      case YEARLY -> this.baseDate = this.baseDate.plusYears(1);
    }
    if (this.recurrenceType != EventRecurrenceTypeEnum.ONCE) {
      this.status = EventStatusEnum.PENDING;
    }
    this.updatedAt = OffsetDateTime.now();
  }

  public void undo() {
    if (this.lastCompletedDate != null) {
      this.baseDate = this.lastCompletedDate;
    }
    this.lastCompletedDate = null;
    this.status = EventStatusEnum.PENDING;
    this.updatedAt = OffsetDateTime.now();
  }
}
