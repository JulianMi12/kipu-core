package com.kipu.core.contacts.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserTagTest {

  @Test
  void create_ShouldNormalizeName() {
    UUID ownerId = UUID.randomUUID();
    UserTag tag = UserTag.create(ownerId, "  PERSONAL  ", "#FFF");

    assertNotNull(tag.getId());
    assertEquals(ownerId, tag.getOwnerUserId());
    assertEquals("personal", tag.getName());
    assertEquals("#FFF", tag.getColorHex());
  }

  @Test
  void reconstitute_ShouldNormalizeName() {
    UUID id = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    UserTag tag = UserTag.reconstitute(id, ownerId, " TRABAJO ", "#000");

    assertEquals(id, tag.getId());
    assertEquals("trabajo", tag.getName());
  }

  @Test
  void update_ShouldNormalizeNameAndSetColor() {
    UserTag tag = UserTag.reconstitute(UUID.randomUUID(), UUID.randomUUID(), "old", "#000");
    tag.update(" NEW NAME ", "#123");

    assertEquals("new name", tag.getName());
    assertEquals("#123", tag.getColorHex());
  }
}
