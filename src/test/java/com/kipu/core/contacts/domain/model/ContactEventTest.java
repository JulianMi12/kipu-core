package com.kipu.core.contacts.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import com.kipu.core.contacts.domain.model.enums.EventRecurrenceTypeEnum;
import com.kipu.core.contacts.domain.model.enums.EventStatusEnum;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ContactEventTest {

  private final UUID contactId = UUID.randomUUID();
  private final LocalDate baseDate = LocalDate.of(2026, 4, 10);

  @Test
  void create_ShouldReturnNewContactEvent_WhenValidDataIsProvided() {
    // Arrange
    String title = "Renovar Pasaporte";
    String description = "Vence pronto";
    int alertLeadTimeDays = 90;

    // Act
    ContactEvent event =
        ContactEvent.create(
            contactId,
            title,
            description,
            baseDate,
            alertLeadTimeDays,
            EventRecurrenceTypeEnum.ONCE);

    // Assert
    assertThat(event.getId()).isNotNull();
    assertThat(event.getContactId()).isEqualTo(contactId);
    assertThat(event.getStatus()).isEqualTo(EventStatusEnum.PENDING);
    assertThat(event.getCreatedAt()).isCloseTo(OffsetDateTime.now(), within(1, ChronoUnit.SECONDS));
  }

  @Test
  void reconstitute_ShouldReturnContactEvent_WhenAllFieldsAreProvided() {
    // Arrange
    UUID id = UUID.randomUUID();
    OffsetDateTime now = OffsetDateTime.now();

    // Act
    ContactEvent event =
        ContactEvent.reconstitute(
            id,
            contactId,
            "Title",
            "Desc",
            baseDate,
            5,
            EventRecurrenceTypeEnum.ONCE,
            EventStatusEnum.COMPLETED,
            baseDate,
            now,
            now);

    // Assert
    assertThat(event.getId()).isEqualTo(id);
    assertThat(event.getStatus()).isEqualTo(EventStatusEnum.COMPLETED);
    assertThat(event.getCreatedAt()).isEqualTo(now);
  }

  @Test
  void update_ShouldModifyFieldsAndSetUpdatedAt_WhenCalled() {
    // Arrange
    ContactEvent event =
        ContactEvent.create(contactId, "Old", "Old", baseDate, 1, EventRecurrenceTypeEnum.ONCE);
    String newTitle = "New Title";
    LocalDate newDate = baseDate.plusDays(1);

    // Act
    event.update(newTitle, "New Desc", newDate, 10, EventRecurrenceTypeEnum.DAILY);

    // Assert
    assertThat(event.getTitle()).isEqualTo(newTitle);
    assertThat(event.getBaseDate()).isEqualTo(newDate);
    assertThat(event.getRecurrenceType()).isEqualTo(EventRecurrenceTypeEnum.DAILY);
    assertThat(event.getUpdatedAt()).isCloseTo(OffsetDateTime.now(), within(1, ChronoUnit.SECONDS));
  }

  @Test
  void complete_ShouldSetStatusToCompleted_WhenRecurrenceIsOnce() {
    // Arrange
    ContactEvent event =
        ContactEvent.create(contactId, "Title", "Desc", baseDate, 10, EventRecurrenceTypeEnum.ONCE);

    // Act
    event.complete();

    // Assert
    assertThat(event.getLastCompletedDate()).isEqualTo(LocalDate.now());
    assertThat(event.getStatus()).isEqualTo(EventStatusEnum.COMPLETED);
    assertThat(event.getBaseDate()).isEqualTo(baseDate);
  }

  @Test
  void complete_ShouldAdvanceBaseDateByOneDay_WhenRecurrenceIsDaily() {
    // Arrange
    ContactEvent event =
        ContactEvent.create(contactId, "Title", "Desc", baseDate, 0, EventRecurrenceTypeEnum.DAILY);

    // Act
    event.complete();

    // Assert
    assertThat(event.getBaseDate()).isEqualTo(baseDate.plusDays(1));
    assertThat(event.getStatus()).isEqualTo(EventStatusEnum.PENDING);
  }

  @Test
  void complete_ShouldAdvanceBaseDateByOneWeek_WhenRecurrenceIsWeekly() {
    // Arrange
    ContactEvent event =
        ContactEvent.create(
            contactId, "Title", "Desc", baseDate, 0, EventRecurrenceTypeEnum.WEEKLY);

    // Act
    event.complete();

    // Assert
    assertThat(event.getBaseDate()).isEqualTo(baseDate.plusWeeks(1));
  }

  @Test
  void complete_ShouldAdvanceBaseDateAndKeepPending_WhenRecurrenceIsMonthly() {
    // Arrange
    ContactEvent event =
        ContactEvent.create(
            contactId, "Gym", "Pagar", baseDate, 2, EventRecurrenceTypeEnum.MONTHLY);

    // Act
    event.complete();

    // Assert
    assertThat(event.getLastCompletedDate()).isEqualTo(LocalDate.now());
    assertThat(event.getStatus()).isEqualTo(EventStatusEnum.PENDING);
    assertThat(event.getBaseDate()).isEqualTo(baseDate.plusMonths(1));
  }

  @Test
  void complete_ShouldAdvanceBaseDateByOneYear_WhenRecurrenceIsYearly() {
    // Arrange
    ContactEvent event =
        ContactEvent.create(
            contactId, "Title", "Desc", baseDate, 0, EventRecurrenceTypeEnum.YEARLY);

    // Act
    event.complete();

    // Assert
    assertThat(event.getBaseDate()).isEqualTo(baseDate.plusYears(1));
  }

  @Test
  void undo_ShouldRevertToPending_WhenEventWasCompleted() {
    // Arrange
    ContactEvent event =
        ContactEvent.create(
            contactId, "Gym", "Pagar", baseDate, 2, EventRecurrenceTypeEnum.MONTHLY);
    event.complete();
    LocalDate dateWhenCompleted = LocalDate.now();

    // Act
    event.undo();

    // Assert
    assertThat(event.getLastCompletedDate()).isNull();
    assertThat(event.getStatus()).isEqualTo(EventStatusEnum.PENDING);
    assertThat(event.getBaseDate()).isEqualTo(dateWhenCompleted);
  }

  @Test
  void undo_ShouldOnlyResetStatus_WhenLastCompletedDateIsNull() {
    // Arrange
    ContactEvent event =
        ContactEvent.create(contactId, "Title", "Desc", baseDate, 0, EventRecurrenceTypeEnum.ONCE);
    // Note: status is already PENDING, lastCompletedDate is null

    // Act
    event.undo();

    // Assert
    assertThat(event.getStatus()).isEqualTo(EventStatusEnum.PENDING);
    assertThat(event.getLastCompletedDate()).isNull();
    assertThat(event.getBaseDate()).isEqualTo(baseDate);
  }
}
