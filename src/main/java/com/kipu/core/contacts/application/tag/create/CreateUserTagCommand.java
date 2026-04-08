package com.kipu.core.contacts.application.tag.create;

import java.util.UUID;

public record CreateUserTagCommand(UUID ownerUserId, String name, String colorHex) {}
