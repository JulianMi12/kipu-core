package com.kipu.core.contacts.application.search;

import com.kipu.core.contacts.domain.model.SearchResultItem;
import com.kipu.core.contacts.domain.model.UserTag;
import java.util.Map;
import java.util.UUID;

public record GlobalSearchResult(UUID id, String type, String title, String subtitle, TagInfo tag) {

  public record TagInfo(String name, String colorHex) {}

  public static GlobalSearchResult from(SearchResultItem item, Map<UUID, UserTag> tagMap) {
    TagInfo tag = null;
    if (item.tagId() != null && tagMap.containsKey(item.tagId())) {
      UserTag userTag = tagMap.get(item.tagId());
      tag = new TagInfo(userTag.getName(), userTag.getColorHex());
    }
    return new GlobalSearchResult(item.id(), item.type(), item.title(), item.subtitle(), tag);
  }
}
