package com.kipu.core.contacts.application.tag.create;

import com.kipu.core.contacts.domain.model.UserTag;
import java.util.UUID;

public record CreateUserTagResult(UUID tagId, String name, String colorHex) {

  public static CreateUserTagResult from(UserTag tag) {
    return new CreateUserTagResult(tag.getId(), tag.getName(), tag.getColorHex());
  }
}
