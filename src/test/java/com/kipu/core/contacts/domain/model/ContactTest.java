package com.kipu.core.contacts.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContactTest {

  @Test
  @DisplayName(
      "createSelfContact: Should create a new contact with generated ID and current timestamp")
  void createSelfContact_ShouldCreateNewContact() {
    // Arrange
    UUID ownerUserId = UUID.randomUUID();
    String firstName = "Julian";
    String lastName = "Miranda";
    String email = "dev@kipu.com";
    LocalDate birthdate = LocalDate.of(1990, 1, 1);
    Map<String, Object> attributes = Map.of("key", "value");

    // Act
    Contact contact =
        Contact.createSelfContact(ownerUserId, firstName, lastName, email, birthdate, attributes);

    // Assert
    assertNotNull(contact.getId());
    assertNotNull(contact.getCreatedAt());
    assertEquals(ownerUserId, contact.getOwnerUserId());
    assertEquals(firstName, contact.getFirstName());
    assertEquals(lastName, contact.getLastName());
    assertEquals(email, contact.getPrimaryEmail());
    assertEquals(birthdate, contact.getBirthdate());
    assertEquals(attributes, contact.getDynamicAttributes());
  }

  @Test
  @DisplayName("reconstitute: Should recreate contact with provided ID and timestamp")
  void reconstitute_ShouldRecreateContactWithExistingData() {
    // Arrange
    UUID existingId = UUID.randomUUID();
    UUID ownerUserId = UUID.randomUUID();
    OffsetDateTime fixedCreatedAt = OffsetDateTime.now().minusDays(1);
    String firstName = "Julian";
    String lastName = "Miranda";
    String email = "dev@kipu.com";
    LocalDate birthdate = LocalDate.of(1990, 1, 1);
    Map<String, Object> attributes = Map.of("sync", true);

    // Act
    Contact contact =
        Contact.reconstitute(
            existingId,
            ownerUserId,
            firstName,
            lastName,
            email,
            birthdate,
            attributes,
            fixedCreatedAt);

    // Assert
    assertEquals(existingId, contact.getId());
    assertEquals(ownerUserId, contact.getOwnerUserId());
    assertEquals(fixedCreatedAt, contact.getCreatedAt());
    assertEquals(firstName, contact.getFirstName());
    assertEquals(lastName, contact.getLastName());
    assertEquals(email, contact.getPrimaryEmail());
    assertEquals(birthdate, contact.getBirthdate());
    assertEquals(attributes, contact.getDynamicAttributes());
  }
}
