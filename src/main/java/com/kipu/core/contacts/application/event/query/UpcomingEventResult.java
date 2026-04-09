package com.kipu.core.contacts.application.event.query;

import com.kipu.core.contacts.domain.model.ContactEvent;
import com.kipu.core.contacts.domain.model.UserTag;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record UpcomingEventResult(
    UUID id, String title, OffsetDateTime startDateTime, List<TagInfo> tags) {

  public record TagInfo(String name, String colorHex) {}

  public static UpcomingEventResult from(ContactEvent event, Map<UUID, UserTag> tagMap) {
    List<TagInfo> tags =
        event.getTagIds().stream()
            .filter(tagMap::containsKey)
            .map(id -> new TagInfo(tagMap.get(id).getName(), tagMap.get(id).getColorHex()))
            .toList();

    return new UpcomingEventResult(event.getId(), event.getTitle(), event.getStartDateTime(), tags);
  }
}
