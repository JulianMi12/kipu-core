package com.kipu.core.contacts.application.contact.create;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record CreateContactCommand(
    UUID ownerUserId,
    String firstName,
    String lastName,
    String primaryEmail,
    LocalDate birthdate,
    Map<String, Object> dynamicAttributes,
    Set<UUID> tagIds,
    String timezone) {}
