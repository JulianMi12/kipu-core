package com.kipu.core.contacts.application.contact.get;

import com.kipu.core.contacts.domain.model.Contact;
import java.time.LocalDate;
import java.time.Period;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record ContactDetailResult(
    UUID id,
    String firstName,
    String lastName,
    String primaryEmail,
    LocalDate birthdate,
    Integer age,
    Map<String, Object> dynamicAttributes,
    Set<UUID> tagIds) {

  public static ContactDetailResult from(Contact contact) {
    Integer calculatedAge = null;

    if (contact.getBirthdate() != null) {
      calculatedAge = Period.between(contact.getBirthdate(), LocalDate.now()).getYears();
    }

    return new ContactDetailResult(
        contact.getId(),
        contact.getFirstName(),
        contact.getLastName(),
        contact.getPrimaryEmail(),
        contact.getBirthdate(),
        calculatedAge,
        contact.getDynamicAttributes(),
        contact.getTagIds());
  }
}
