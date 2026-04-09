package com.kipu.core.contacts.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.kipu.core.contacts.domain.model.enums.EventRecurrenceTypeEnum;
import com.kipu.core.contacts.domain.model.enums.EventStatusEnum;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ContactEventTest {

  private final UUID contactId = UUID.randomUUID();
  private final OffsetDateTime baseDate = OffsetDateTime.parse("2026-04-10T09:00:00Z");
  private final String defaultTz = "UTC";

  @Test
  @DisplayName("Constructor: Should be private (Coverage for Lombok/Constructor lines)")
  void constructor_ShouldBePrivate() throws NoSuchMethodException {
    Constructor<ContactEvent> constructor =
        ContactEvent.class.getDeclaredConstructor(
            UUID.class,
            UUID.class,
            String.class,
            String.class,
            OffsetDateTime.class,
            int.class,
            EventRecurrenceTypeEnum.class,
            int.class,
            EventStatusEnum.class,
            OffsetDateTime.class,
            String.class,
            Set.class,
            OffsetDateTime.class,
            OffsetDateTime.class);
    assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
  }

  @Test
  @DisplayName("create: Should cover all ternary branches for null tags and timezone")
  void create_ShouldHandleAllNullBranches() {
    // Rama: timezone null -> "UTC", tagIds null -> HashSet vacío
    ContactEvent event =
        ContactEvent.create(
            contactId, "Title", "Desc", baseDate, 0, EventRecurrenceTypeEnum.ONCE, 1, null, null);

    assertThat(event.getTimezone()).isEqualTo("UTC");
    assertThat(event.getTagIds()).isNotNull().isEmpty();
    assertThat(event.getStatus()).isEqualTo(EventStatusEnum.PENDING);
  }

  @Test
  @DisplayName("reconstitute: Should handle null tagIds branch")
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
            1,
            EventStatusEnum.PENDING,
            null,
            defaultTz,
            null,
            OffsetDateTime.now(),
            OffsetDateTime.now());

    assertThat(event.getTagIds()).isNotNull().isEmpty();
  }

  @Test
  @DisplayName("update: Should handle null tags branch and update timestamp")
  void update_ShouldHandleNullTagsAndModifyFields() {
    ContactEvent event =
        ContactEvent.create(
            contactId,
            "Old",
            "D",
            baseDate,
            0,
            EventRecurrenceTypeEnum.ONCE,
            1,
            defaultTz,
            Set.of(UUID.randomUUID()));
    OffsetDateTime oldUpdate = event.getUpdatedAt();

    event.update(
        "New",
        "ND",
        baseDate.plusDays(1),
        5,
        EventRecurrenceTypeEnum.DAILY,
        1,
        "America/Bogota",
        null);

    assertThat(event.getTitle()).isEqualTo("New");
    assertThat(event.getTagIds()).isEmpty();
    assertThat(event.getUpdatedAt()).isAfterOrEqualTo(oldUpdate);
  }

  @ParameterizedTest
  @EnumSource(EventRecurrenceTypeEnum.class)
  @DisplayName("complete: Should cover 100% of switch cases and status branches")
  void complete_ShouldCoverAllBranches(EventRecurrenceTypeEnum type) {
    // Arrange
    int interval = 3; // Para probar HOURLY con intervalo
    ContactEvent event =
        ContactEvent.create(
            contactId, "Task", "Desc", baseDate, 0, type, interval, defaultTz, null);

    OffsetDateTime preCompleteUpdate = event.getUpdatedAt();

    // Act
    event.complete();

    // Assert
    assertThat(event.getLastCompletedDate()).isNotNull();
    assertThat(event.getUpdatedAt()).isAfterOrEqualTo(preCompleteUpdate);

    switch (type) {
      case ONCE -> {
        assertThat(event.getStatus()).isEqualTo(EventStatusEnum.COMPLETED);
        assertThat(event.getStartDateTime()).isEqualTo(baseDate);
      }
      case HOURLY -> {
        assertThat(event.getStartDateTime()).isEqualTo(baseDate.plusHours(interval));
        assertThat(event.getStatus()).isEqualTo(EventStatusEnum.PENDING);
      }
      case DAILY -> assertThat(event.getStartDateTime()).isEqualTo(baseDate.plusDays(1));
      case WEEKLY -> assertThat(event.getStartDateTime()).isEqualTo(baseDate.plusWeeks(1));
      case MONTHLY -> assertThat(event.getStartDateTime()).isEqualTo(baseDate.plusMonths(1));
      case YEARLY -> assertThat(event.getStartDateTime()).isEqualTo(baseDate.plusYears(1));
    }

    if (type != EventRecurrenceTypeEnum.ONCE) {
      assertThat(event.getStatus()).isEqualTo(EventStatusEnum.PENDING);
    }
  }

  @Test
  @DisplayName("undo: Should reset state and update timestamp")
  void undo_ShouldResetStatusAndClearDate() {
    ContactEvent event =
        ContactEvent.create(
            contactId, "T", "D", baseDate, 0, EventRecurrenceTypeEnum.ONCE, 1, defaultTz, null);
    event.complete();
    OffsetDateTime lastUpdate = event.getUpdatedAt();

    event.undo();

    assertThat(event.getLastCompletedDate()).isNull();
    assertThat(event.getStatus()).isEqualTo(EventStatusEnum.PENDING);
    assertThat(event.getUpdatedAt()).isAfterOrEqualTo(lastUpdate);
  }

  @Test
  @DisplayName("updateStartDateTime: Should cover both normal and exception branches")
  void updateStartDateTime_ShouldWorkOrThrow() {
    ContactEvent event =
        ContactEvent.create(
            contactId, "T", "D", baseDate, 0, EventRecurrenceTypeEnum.ONCE, 1, defaultTz, null);

    OffsetDateTime newDate = baseDate.plusDays(10);
    event.updateStartDateTime(newDate);
    assertThat(event.getStartDateTime()).isEqualTo(newDate);

    assertThatThrownBy(() -> event.updateStartDateTime(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("La fecha de inicio no puede ser nula");
  }

  @Test
  @DisplayName("tagIds: Should ensure a new HashSet is created (defensive copying)")
  void tagIds_ShouldEnsureMutablityAndDefensiveCopy() {
    Set<UUID> tags = Set.of(UUID.randomUUID());
    ContactEvent event =
        ContactEvent.reconstitute(
            UUID.randomUUID(),
            contactId,
            "T",
            "D",
            baseDate,
            0,
            EventRecurrenceTypeEnum.ONCE,
            1,
            EventStatusEnum.PENDING,
            null,
            defaultTz,
            tags,
            OffsetDateTime.now(),
            OffsetDateTime.now());

    assertThat(event.getTagIds()).isNotSameAs(tags);
    assertThat(event.getTagIds()).containsAll(tags);
  }

  @Test
  @DisplayName("create: Branch Coverage - tagIds != null is TRUE")
  void create_ShouldHandleNonNullTagsBranch() {
    // Arrange
    Set<UUID> tags = Set.of(UUID.randomUUID(), UUID.randomUUID());

    // Act
    ContactEvent event =
        ContactEvent.create(
            contactId, "Title", "Desc", baseDate, 0, EventRecurrenceTypeEnum.ONCE, 1, "UTC", tags);

    // Assert
    assertThat(event.getTagIds()).isNotNull().hasSize(2).containsAll(tags);
    // Verificamos mutabilidad (HashSet)
    assertThat(event.getTagIds()).isInstanceOf(java.util.HashSet.class);
  }

  @Test
  @DisplayName("reconstitute: Branch Coverage - tagIds != null is TRUE")
  void reconstitute_ShouldHandleNonNullTagsBranch() {
    // Arrange
    Set<UUID> tags = Set.of(UUID.randomUUID());
    OffsetDateTime now = OffsetDateTime.now();

    // Act
    ContactEvent event =
        ContactEvent.reconstitute(
            UUID.randomUUID(),
            contactId,
            "T",
            "D",
            baseDate,
            0,
            EventRecurrenceTypeEnum.ONCE,
            1,
            EventStatusEnum.PENDING,
            null,
            "UTC",
            tags,
            now,
            now);

    // Assert
    assertThat(event.getTagIds()).hasSize(1).containsAll(tags);
  }

  @Test
  @DisplayName("update: Branch Coverage - tagIds != null is TRUE")
  void update_ShouldHandleNonNullTagsBranch() {
    // Arrange
    ContactEvent event =
        ContactEvent.create(
            contactId, "T", "D", baseDate, 0, EventRecurrenceTypeEnum.ONCE, 1, "UTC", null);
    Set<UUID> newTags = Set.of(UUID.randomUUID());

    // Act
    event.update("T2", "D2", baseDate, 0, EventRecurrenceTypeEnum.ONCE, 1, "UTC", newTags);

    // Assert
    assertThat(event.getTagIds()).hasSize(1).containsAll(newTags);
  }
}
