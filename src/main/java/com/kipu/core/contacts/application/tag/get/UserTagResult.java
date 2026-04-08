package com.kipu.core.contacts.application.tag.get;

import com.kipu.core.contacts.domain.model.UserTag;
import java.util.UUID;

public record UserTagResult(UUID tagId, String name, String colorHex) {

  public static UserTagResult from(UserTag tag) {
    return new UserTagResult(tag.getId(), tag.getName(), tag.getColorHex());
  }
}
