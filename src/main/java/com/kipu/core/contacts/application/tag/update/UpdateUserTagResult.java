package com.kipu.core.contacts.application.tag.update;

import com.kipu.core.contacts.domain.model.UserTag;
import java.util.UUID;

public record UpdateUserTagResult(UUID tagId, String name, String colorHex) {

  public static UpdateUserTagResult from(UserTag tag) {
    return new UpdateUserTagResult(tag.getId(), tag.getName(), tag.getColorHex());
  }
}
