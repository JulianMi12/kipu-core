package com.kipu.core.contacts.application.tag.update;

import java.util.UUID;

public record UpdateUserTagCommand(
    UUID authenticatedUserId, UUID tagId, String name, String colorHex) {}
