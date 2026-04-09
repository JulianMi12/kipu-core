package com.kipu.core.contacts.application.contact.update;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record UpdateContactCommand(
    UUID authenticatedUserId,
    UUID contactId,
    String firstName,
    String lastName,
    String primaryEmail,
    LocalDate birthdate,
    Map<String, Object> dynamicAttributes,
    Set<UUID> tagIds,
    String timezone) {}
