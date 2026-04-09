package com.kipu.core.contacts.domain.model;

import com.kipu.core.contacts.domain.model.enums.EventRecurrenceTypeEnum;
import com.kipu.core.contacts.domain.model.enums.EventStatusEnum;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
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
  private OffsetDateTime startDateTime;
  private int alertLeadTimeDays;
  private EventRecurrenceTypeEnum recurrenceType;
  private int recurrenceInterval;
  private EventStatusEnum status;
  private OffsetDateTime lastCompletedDate;
  private String timezone;
  private Set<UUID> tagIds;
  private final OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;

  public static ContactEvent create(
      UUID contactId,
      String title,
      String description,
      OffsetDateTime startDateTime,
      int alertLeadTimeDays,
      EventRecurrenceTypeEnum recurrenceType,
      int recurrenceInterval,
      String timezone,
      Set<UUID> tagIds) {
    OffsetDateTime now = OffsetDateTime.now();
    return new ContactEvent(
        UUID.randomUUID(),
        contactId,
        title,
        description,
        startDateTime,
        alertLeadTimeDays,
        recurrenceType,
        recurrenceInterval,
        EventStatusEnum.PENDING,
        null,
        timezone != null ? timezone : "UTC",
        tagIds != null ? new HashSet<>(tagIds) : new HashSet<>(),
        now,
        now);
  }

  public static ContactEvent reconstitute(
      UUID id,
      UUID contactId,
      String title,
      String description,
      OffsetDateTime startDateTime,
      int alertLeadTimeDays,
      EventRecurrenceTypeEnum recurrenceType,
      int recurrenceInterval,
      EventStatusEnum status,
      OffsetDateTime lastCompletedDate,
      String timezone,
      Set<UUID> tagIds,
      OffsetDateTime createdAt,
      OffsetDateTime updatedAt) {
    return new ContactEvent(
        id,
        contactId,
        title,
        description,
        startDateTime,
        alertLeadTimeDays,
        recurrenceType,
        recurrenceInterval,
        status,
        lastCompletedDate,
        timezone,
        tagIds != null ? new HashSet<>(tagIds) : new HashSet<>(),
        createdAt,
        updatedAt);
  }

  public void update(
      String title,
      String description,
      OffsetDateTime startDateTime,
      int alertLeadTimeDays,
      EventRecurrenceTypeEnum recurrenceType,
      int recurrenceInterval,
      String timezone,
      Set<UUID> tagIds) {
    this.title = title;
    this.description = description;
    this.startDateTime = startDateTime;
    this.alertLeadTimeDays = alertLeadTimeDays;
    this.recurrenceType = recurrenceType;
    this.recurrenceInterval = recurrenceInterval;
    this.timezone = timezone;
    this.tagIds = tagIds != null ? new HashSet<>(tagIds) : new HashSet<>();
    this.updatedAt = OffsetDateTime.now();
  }

  public void complete() {
    this.lastCompletedDate = OffsetDateTime.now();

    switch (this.recurrenceType) {
      case ONCE -> this.status = EventStatusEnum.COMPLETED;
      case HOURLY -> this.startDateTime = this.startDateTime.plusHours(this.recurrenceInterval);
      case DAILY -> this.startDateTime = this.startDateTime.plusDays(1);
      case WEEKLY -> this.startDateTime = this.startDateTime.plusWeeks(1);
      case MONTHLY -> this.startDateTime = this.startDateTime.plusMonths(1);
      case YEARLY -> this.startDateTime = this.startDateTime.plusYears(1);
    }

    if (this.recurrenceType != EventRecurrenceTypeEnum.ONCE) {
      this.status = EventStatusEnum.PENDING;
    }
    this.updatedAt = OffsetDateTime.now();
  }

  public void undo() {
    this.lastCompletedDate = null;
    this.status = EventStatusEnum.PENDING;
    this.updatedAt = OffsetDateTime.now();
  }

  public void updateStartDateTime(OffsetDateTime newStartDateTime) {
    if (newStartDateTime == null) {
      throw new IllegalArgumentException("La fecha de inicio no puede ser nula");
    }

    this.startDateTime = newStartDateTime;
  }
}
