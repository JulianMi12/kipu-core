package com.kipu.core.contacts.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.kipu.core.contacts.domain.model.enums.EventRecurrenceTypeEnum;
import com.kipu.core.contacts.domain.model.enums.EventStatusEnum;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContactEventTest {

  private final UUID contactId = UUID.randomUUID();
  private final LocalDate baseDate = LocalDate.of(2026, 4, 10);

  @Test
  @DisplayName("create: Should initialize with tags or empty set if null")
  void create_ShouldInitializeCorrectly() {
    // Caso con tags
    UUID tagId = UUID.randomUUID();
    ContactEvent event =
        ContactEvent.create(
            contactId, "T", "D", baseDate, 0, EventRecurrenceTypeEnum.ONCE, Set.of(tagId));
    assertThat(event.getTagIds()).containsExactly(tagId);

    // Caso con tags nulos (Cubre rama ternaria)
    ContactEvent eventNullTags =
        ContactEvent.create(contactId, "T", "D", baseDate, 0, EventRecurrenceTypeEnum.ONCE, null);
    assertThat(eventNullTags.getTagIds()).isEmpty();
  }

  @Test
  @DisplayName("reconstitute: Should handle null tags correctly")
  void reconstitute_ShouldHandleNullTags() {
    ContactEvent event =
        ContactEvent.reconstitute(
            UUID.randomUUID(),
            contactId,
            "T",
            "D",
            baseDate,
            0,
            EventRecurrenceTypeEnum.ONCE,
            EventStatusEnum.PENDING,
            null,
            null,
            OffsetDateTime.now(),
            OffsetDateTime.now());

    assertThat(event.getTagIds()).isEmpty();
  }

  @Test
  @DisplayName("update: Should modify all fields and update timestamp")
  void update_ShouldWork() {
    ContactEvent event =
        ContactEvent.create(
            contactId, "Old", "D", baseDate, 0, EventRecurrenceTypeEnum.ONCE, Set.of());
    OffsetDateTime oldUpdate = event.getUpdatedAt();

    event.update(
        "New",
        "ND",
        baseDate.plusDays(1),
        5,
        EventRecurrenceTypeEnum.DAILY,
        Set.of(UUID.randomUUID()));

    assertThat(event.getTitle()).isEqualTo("New");
    assertThat(event.getUpdatedAt()).isAfterOrEqualTo(oldUpdate);
  }

  @Test
  @DisplayName("complete: Should cover all switch cases and status logic")
  void complete_ShouldCoverAllRecurrences() {
    // Caso ONCE -> COMPLETED
    ContactEvent once =
        ContactEvent.create(contactId, "T", "D", baseDate, 0, EventRecurrenceTypeEnum.ONCE, null);
    once.complete();
    assertThat(once.getStatus()).isEqualTo(EventStatusEnum.COMPLETED);

    // Caso DAILY -> PENDING + 1 day
    ContactEvent daily =
        ContactEvent.create(contactId, "T", "D", baseDate, 0, EventRecurrenceTypeEnum.DAILY, null);
    daily.complete();
    assertThat(daily.getBaseDate()).isEqualTo(baseDate.plusDays(1));
    assertThat(daily.getStatus()).isEqualTo(EventStatusEnum.PENDING);

    // Caso WEEKLY -> + 1 week
    ContactEvent weekly =
        ContactEvent.create(contactId, "T", "D", baseDate, 0, EventRecurrenceTypeEnum.WEEKLY, null);
    weekly.complete();
    assertThat(weekly.getBaseDate()).isEqualTo(baseDate.plusWeeks(1));

    // Caso MONTHLY -> + 1 month
    ContactEvent monthly =
        ContactEvent.create(
            contactId, "T", "D", baseDate, 0, EventRecurrenceTypeEnum.MONTHLY, null);
    monthly.complete();
    assertThat(monthly.getBaseDate()).isEqualTo(baseDate.plusMonths(1));

    // Caso YEARLY -> + 1 year
    ContactEvent yearly =
        ContactEvent.create(contactId, "T", "D", baseDate, 0, EventRecurrenceTypeEnum.YEARLY, null);
    yearly.complete();
    assertThat(yearly.getBaseDate()).isEqualTo(baseDate.plusYears(1));
  }

  @Test
  @DisplayName("undo: Should revert baseDate to lastCompletedDate if present")
  void undo_ShouldHandleLastCompletedDate() {
    // Arrange: Creamos un evento, lo completamos (esto guarda la fecha de hoy en lastCompletedDate)
    ContactEvent event =
        ContactEvent.create(
            contactId, "T", "D", baseDate, 0, EventRecurrenceTypeEnum.MONTHLY, null);
    event.complete();
    LocalDate completedDate = event.getLastCompletedDate(); // Es LocalDate.now()

    // Act
    event.undo();

    // Assert: baseDate debe ser ahora la fecha en que se completó
    assertThat(event.getBaseDate()).isEqualTo(completedDate);
    assertThat(event.getLastCompletedDate()).isNull();
    assertThat(event.getStatus()).isEqualTo(EventStatusEnum.PENDING);
  }

  @Test
  @DisplayName("undo: Should work even if lastCompletedDate is null")
  void undo_ShouldWorkWithNullLastDate() {
    ContactEvent event =
        ContactEvent.create(contactId, "T", "D", baseDate, 0, EventRecurrenceTypeEnum.ONCE, null);
    // lastCompletedDate es nulo porque nunca se completó

    event.undo();

    assertThat(event.getStatus()).isEqualTo(EventStatusEnum.PENDING);
    assertThat(event.getBaseDate()).isEqualTo(baseDate); // No cambia
  }

  @Test
  @DisplayName("reconstitute: Should initialize HashSet with provided tags when tagIds is not null")
  void reconstitute_ShouldHandleNonNullTags() {
    // Arrange
    UUID id = UUID.randomUUID();
    UUID tagId1 = UUID.randomUUID();
    UUID tagId2 = UUID.randomUUID();
    Set<UUID> inputTags = Set.of(tagId1, tagId2);
    OffsetDateTime now = OffsetDateTime.now();

    // Act
    ContactEvent event =
        ContactEvent.reconstitute(
            id,
            contactId,
            "Title",
            "Description",
            baseDate,
            5,
            EventRecurrenceTypeEnum.WEEKLY,
            EventStatusEnum.PENDING,
            null,
            inputTags, // Pasamos un Set no nulo
            now,
            now);

    // Assert
    assertThat(event.getTagIds()).isNotNull().hasSize(2).containsExactlyInAnyOrder(tagId1, tagId2);

    // Verificamos que sea una nueva instancia de HashSet (por el requerimiento de mutabilidad)
    assertThat(event.getTagIds()).isNotSameAs(inputTags);
  }
}
