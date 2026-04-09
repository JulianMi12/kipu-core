package com.kipu.core.contacts.application.contact.get;

import com.kipu.core.contacts.domain.model.Contact;
import com.kipu.core.contacts.domain.model.UserTag;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record ContactDetailResult(
    UUID id,
    String firstName,
    String lastName,
    String primaryEmail,
    LocalDate birthdate,
    Integer age,
    Map<String, Object> dynamicAttributes,
    List<TagInfo> tags) {

  public record TagInfo(String name, String colorHex) {}

  public static ContactDetailResult from(Contact contact, List<UserTag> userTags) {
    Integer calculatedAge = null;

    if (contact.getBirthdate() != null) {
      calculatedAge = Period.between(contact.getBirthdate(), LocalDate.now()).getYears();
    }

    List<TagInfo> tagInfoList =
        userTags.stream().map(tag -> new TagInfo(tag.getName(), tag.getColorHex())).toList();

    return new ContactDetailResult(
        contact.getId(),
        contact.getFirstName(),
        contact.getLastName(),
        contact.getPrimaryEmail(),
        contact.getBirthdate(),
        calculatedAge,
        contact.getDynamicAttributes(),
        tagInfoList);
  }
}
