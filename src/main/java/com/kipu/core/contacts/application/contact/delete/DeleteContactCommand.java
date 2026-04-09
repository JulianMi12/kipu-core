package com.kipu.core.contacts.application.contact.delete;

import java.util.UUID;

public record DeleteContactCommand(UUID authenticatedUserId, UUID contactId) {}
