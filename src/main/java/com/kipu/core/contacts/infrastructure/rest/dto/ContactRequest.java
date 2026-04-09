package com.kipu.core.contacts.infrastructure.rest.dto;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record ContactRequest(
    String firstName,
    String lastName,
    String primaryEmail,
    LocalDate birthdate,
    Map<String, Object> dynamicAttributes,
    Set<UUID> tagIds) {}
