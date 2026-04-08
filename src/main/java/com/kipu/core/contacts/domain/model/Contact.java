package com.kipu.core.contacts.domain.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
  private Set<UUID> tagIds;
  private final OffsetDateTime createdAt;

  public static Contact createSelfContact(
      UUID ownerUserId,
      String firstName,
      String lastName,
      String primaryEmail,
      LocalDate birthdate,
      Map<String, Object> dynamicAttributes,
      Set<UUID> tagIds) {
    return new Contact(
        UUID.randomUUID(),
        ownerUserId,
        firstName,
        lastName,
        primaryEmail,
        birthdate,
        dynamicAttributes,
        new HashSet<>(tagIds),
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
      Set<UUID> tagIds,
      OffsetDateTime createdAt) {
    return new Contact(
        id,
        ownerUserId,
        firstName,
        lastName,
        primaryEmail,
        birthdate,
        dynamicAttributes,
        new HashSet<>(tagIds),
        createdAt);
  }

  public void update(
      String firstName,
      String lastName,
      String primaryEmail,
      LocalDate birthdate,
      Map<String, Object> dynamicAttributes,
      Set<UUID> tagIds) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.primaryEmail = primaryEmail;
    this.birthdate = birthdate;
    this.dynamicAttributes = dynamicAttributes;
    this.tagIds = new HashSet<>(tagIds);
  }
}
