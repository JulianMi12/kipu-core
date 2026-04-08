package com.kipu.core.contacts.domain.model;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserTag {

  private final UUID id;
  private final UUID ownerUserId;
  private String name;
  private String colorHex;

  public static UserTag create(UUID ownerUserId, String name, String colorHex) {
    return new UserTag(UUID.randomUUID(), ownerUserId, name.trim().toLowerCase(), colorHex);
  }

  public static UserTag reconstitute(UUID id, UUID ownerUserId, String name, String colorHex) {
    return new UserTag(id, ownerUserId, name.trim().toLowerCase(), colorHex);
  }

  public void update(String name, String colorHex) {
    this.name = name.trim().toLowerCase();
    this.colorHex = colorHex;
  }
}
