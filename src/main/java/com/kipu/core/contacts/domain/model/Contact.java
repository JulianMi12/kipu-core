package com.kipu.core.contacts.domain.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Contact {

  private final UUID id;
  private final UUID ownerUserId;
  private String firstName;
  private String lastName;
  private String primaryEmail;
  private LocalDate birthdate;
  private Map<String, Object> dynamicAttributes;
  private final OffsetDateTime createdAt;

  public static Contact createSelfContact(
      UUID ownerUserId,
      String firstName,
      String lastName,
      String primaryEmail,
      LocalDate birthdate,
      Map<String, Object> dynamicAttributes) {
    return new Contact(
        UUID.randomUUID(),
        ownerUserId,
        firstName,
        lastName,
        primaryEmail,
        birthdate,
        dynamicAttributes,
        OffsetDateTime.now());
  }

  public static Contact reconstitute(
      UUID id,
      UUID ownerUserId,
      String firstName,
      String lastName,
      String primaryEmail,
      LocalDate birthdate,
      Map<String, Object> dynamicAttributes,
      OffsetDateTime createdAt) {
    return new Contact(
        id,
        ownerUserId,
        firstName,
        lastName,
        primaryEmail,
        birthdate,
        dynamicAttributes,
        createdAt);
  }
}
