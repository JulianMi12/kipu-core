package com.kipu.core.contacts.application.contact.get;

import com.kipu.core.contacts.domain.model.Contact;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record ContactSummaryResult(
    UUID id, String firstName, String lastName, String primaryEmail, Set<UUID> tagIds) {

  public static ContactSummaryResult from(Contact contact) {
    Set<UUID> limitedTagIds =
        contact.getTagIds() != null
            ? contact.getTagIds().stream().limit(3).collect(Collectors.toUnmodifiableSet())
            : Set.of();
    return new ContactSummaryResult(
        contact.getId(),
        contact.getFirstName(),
        contact.getLastName(),
        contact.getPrimaryEmail(),
        limitedTagIds);
  }
}
