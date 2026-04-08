package com.kipu.core.contacts.application.event.complete;

import java.util.UUID;

public record CompleteContactEventCommand(UUID authenticatedUserId, UUID eventId) {}
