package com.kipu.core.contacts.application.event.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.domain.model.ContactEvent;
import com.kipu.core.contacts.domain.model.UserTag;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UpcomingEventResultTest {

  @Test
  @DisplayName("from: Should map event and enrich tags when all IDs exist in the map")
  void from_ShouldEnrichTags_WhenIdsExistInMap() {
    // Arrange
    UUID eventId = UUID.randomUUID();
    UUID tagId = UUID.randomUUID();
    OffsetDateTime now = OffsetDateTime.now();

    ContactEvent event = mock(ContactEvent.class);
    when(event.getId()).thenReturn(eventId);
    when(event.getTitle()).thenReturn("Cita Médica");
    when(event.getStartDateTime()).thenReturn(now);
    when(event.getTagIds()).thenReturn(Set.of(tagId));

    UserTag tag = UserTag.reconstitute(tagId, UUID.randomUUID(), "Salud", "#00FF00");
    Map<UUID, UserTag> tagMap = Map.of(tagId, tag);

    // Act
    UpcomingEventResult result = UpcomingEventResult.from(event, tagMap);

    // Assert
    assertThat(result.id()).isEqualTo(eventId);
    assertThat(result.title()).isEqualTo("Cita Médica");
    assertThat(result.startDateTime()).isEqualTo(now);
    assertThat(result.tags()).hasSize(1);
    assertThat(result.tags().get(0).name()).isEqualTo("salud");
    assertThat(result.tags().get(0).colorHex()).isEqualTo("#00FF00");
  }

  @Test
  @DisplayName("from: Should ignore tags that are not present in the tagMap (Filter Coverage)")
  void from_ShouldIgnoreMissingTagsInMap() {
    // Arrange
    UUID tagIdInMap = UUID.randomUUID();
    UUID tagIdMissing = UUID.randomUUID();

    ContactEvent event = mock(ContactEvent.class);
    when(event.getTagIds()).thenReturn(Set.of(tagIdInMap, tagIdMissing));

    UserTag tag = UserTag.reconstitute(tagIdInMap, UUID.randomUUID(), "Existente", "#FFF");
    Map<UUID, UserTag> tagMap = Map.of(tagIdInMap, tag);

    // Act
    UpcomingEventResult result = UpcomingEventResult.from(event, tagMap);

    // Assert
    // El tag missing debe ser filtrado por .filter(tagMap::containsKey)
    assertThat(result.tags()).hasSize(1);
    assertThat(result.tags().get(0).name()).isEqualTo("existente");
  }

  @Test
  @DisplayName("from: Should return empty tags list when event has no tagIds")
  void from_ShouldReturnEmptyTags_WhenEventHasNoTags() {
    // Arrange
    ContactEvent event = mock(ContactEvent.class);
    when(event.getTagIds()).thenReturn(Collections.emptySet());
    Map<UUID, UserTag> tagMap = Map.of(UUID.randomUUID(), mock(UserTag.class));

    // Act
    UpcomingEventResult result = UpcomingEventResult.from(event, tagMap);

    // Assert
    assertThat(result.tags()).isEmpty();
  }

  @Test
  @DisplayName("TagInfo: Should verify record integrity")
  void tagInfo_ShouldStoreDataCorrectly() {
    // Test simple para cubrir el constructor del inner record TagInfo
    UpcomingEventResult.TagInfo tagInfo = new UpcomingEventResult.TagInfo("Work", "#000");

    assertThat(tagInfo.name()).isEqualTo("Work");
    assertThat(tagInfo.colorHex()).isEqualTo("#000");
  }
}
