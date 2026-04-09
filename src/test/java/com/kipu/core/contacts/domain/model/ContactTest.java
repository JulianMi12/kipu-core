package com.kipu.core.contacts.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContactTest {

  private final UUID ownerUserId = UUID.randomUUID();
  private final String firstName = "Julian";
  private final String lastName = "Miranda";
  private final String email = "dev@kipu.com";
  private final LocalDate birthdate = LocalDate.of(1990, 1, 1);

  @Test
  @DisplayName("createSelfContact: Should create a new contact with tags and current timestamp")
  void createSelfContact_ShouldCreateNewContact() {
    // Arrange
    Map<String, Object> attributes = Map.of("key", "value");
    UUID tagId = UUID.randomUUID();
    Set<UUID> tags = Set.of(tagId);

    // Act
    Contact contact =
        Contact.create(ownerUserId, firstName, lastName, email, birthdate, attributes, tags);

    // Assert
    assertNotNull(contact.getId());
    assertNotNull(contact.getCreatedAt());
    assertEquals(ownerUserId, contact.getOwnerUserId());
    assertEquals(firstName, contact.getFirstName());
    assertEquals(lastName, contact.getLastName());
    assertEquals(email, contact.getPrimaryEmail());
    assertEquals(birthdate, contact.getBirthdate());
    assertEquals(attributes, contact.getDynamicAttributes());
    assertTrue(contact.getTagIds().contains(tagId));
  }

  @Test
  @DisplayName("reconstitute: Should recreate contact with provided ID, tags and timestamp")
  void reconstitute_ShouldRecreateContactWithExistingData() {
    // Arrange
    UUID existingId = UUID.randomUUID();
    OffsetDateTime fixedCreatedAt = OffsetDateTime.now().minusDays(1);
    Map<String, Object> attributes = Map.of("sync", true);
    UUID tagId = UUID.randomUUID();

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
            Set.of(tagId),
            fixedCreatedAt);

    // Assert
    assertEquals(existingId, contact.getId());
    assertEquals(ownerUserId, contact.getOwnerUserId());
    assertEquals(fixedCreatedAt, contact.getCreatedAt());
    assertTrue(contact.getTagIds().contains(tagId));
  }

  @Test
  @DisplayName("update: Should modify all editable fields including tags")
  void update_ShouldModifyFields() {
    // Arrange
    Contact contact =
        Contact.create(ownerUserId, firstName, lastName, email, birthdate, Map.of(), Set.of());

    String newFirstName = "Julian Updated";
    String newEmail = "updated@kipu.com";
    Map<String, Object> newAttributes = Map.of("version", 2);
    UUID newTagId = UUID.randomUUID();
    Set<UUID> newTags = Set.of(newTagId);

    // Act
    contact.update(newFirstName, lastName, newEmail, birthdate, newAttributes, newTags);

    // Assert
    assertEquals(newFirstName, contact.getFirstName());
    assertEquals(newEmail, contact.getPrimaryEmail());
    assertEquals(newAttributes, contact.getDynamicAttributes());
    assertEquals(1, contact.getTagIds().size());
    assertTrue(contact.getTagIds().contains(newTagId));
  }

  @Test
  @DisplayName("reconstitute: Should handle null tags by setting field to null (Branch Coverage)")
  void reconstitute_ShouldHandleNullTags() {
    // Act
    Contact contact =
        Contact.reconstitute(
            UUID.randomUUID(),
            ownerUserId,
            firstName,
            lastName,
            email,
            birthdate,
            Map.of(),
            null,
            OffsetDateTime.now());

    // Assert
    assertThat(contact.getTagIds()).isNull();
  }

  @Test
  @DisplayName("createSelfContact: Should create independent HashSet for tagIds")
  void createSelfContact_ShouldCreateIndependentHashSet() {
    // Arrange
    Set<UUID> originalTags = new java.util.HashSet<>();
    UUID tagId = UUID.randomUUID();
    originalTags.add(tagId);

    // Act
    Contact contact =
        Contact.create(ownerUserId, firstName, lastName, email, birthdate, Map.of(), originalTags);

    // Modificamos la colección original
    originalTags.clear();

    // Assert
    // El contacto no debe verse afectado porque debe tener su propia instancia de HashSet
    assertThat(contact.getTagIds()).containsExactly(tagId);
  }

  @Test
  @DisplayName("update: Should create independent HashSet for new tagIds")
  void update_ShouldCreateIndependentHashSet() {
    // Arrange
    Contact contact =
        Contact.create(ownerUserId, firstName, lastName, email, birthdate, Map.of(), Set.of());

    Set<UUID> newTags = new java.util.HashSet<>();
    UUID newTagId = UUID.randomUUID();
    newTags.add(newTagId);

    // Act
    contact.update(firstName, lastName, email, birthdate, Map.of(), newTags);
    newTags.clear(); // Modificamos la fuente

    // Assert
    assertThat(contact.getTagIds()).containsExactly(newTagId);
  }
}
