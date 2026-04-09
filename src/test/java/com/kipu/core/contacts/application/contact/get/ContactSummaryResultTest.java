package com.kipu.core.contacts.application.contact.get;

import static org.assertj.core.api.Assertions.assertThat;

import com.kipu.core.contacts.domain.model.Contact;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContactSummaryResultTest {

  @Test
  @DisplayName("from: Should limit tags to a maximum of 3")
  void from_ShouldLimitTagsToThree() {
    // Arrange
    Set<UUID> manyTags =
        Set.of(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID());
    Contact contact = createContactWithTags(manyTags);

    // Act
    ContactSummaryResult result = ContactSummaryResult.from(contact);

    // Assert
    assertThat(result.tagIds()).hasSize(3);
    assertThat(manyTags).containsAll(result.tagIds());
  }

  @Test
  @DisplayName("from: Should return all tags when they are 3 or fewer")
  void from_ShouldReturnAllTags_WhenSizeIsSmall() {
    // Arrange
    Set<UUID> fewTags = Set.of(UUID.randomUUID(), UUID.randomUUID());
    Contact contact = createContactWithTags(fewTags);

    // Act
    ContactSummaryResult result = ContactSummaryResult.from(contact);

    // Assert
    assertThat(result.tagIds()).hasSize(2);
    assertThat(result.tagIds()).containsExactlyInAnyOrderElementsOf(fewTags);
  }

  @Test
  @DisplayName("from: Should handle null tagIds in contact (Branch Coverage 100%)")
  void from_ShouldReturnEmptySet_WhenContactTagIdsIsNull() {
    // Arrange: Creamos el contacto con tagIds nulo explícitamente
    Contact contact =
        Contact.reconstitute(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Carol",
            "Gomez",
            "caro@test.com",
            null,
            Map.of(),
            null, // <--- Esto activará el : Set.of()
            OffsetDateTime.now());

    // Act
    ContactSummaryResult result = ContactSummaryResult.from(contact);

    // Assert
    // IMPORTANTE: Primero verificar que no sea nulo antes de pedir el size
    assertThat(result.tagIds()).isNotNull();
    assertThat(result.tagIds()).isEmpty();
  }

  @Test
  @DisplayName("from: Should map basic contact fields correctly")
  void from_ShouldMapBasicFields() {
    // Arrange
    UUID id = UUID.randomUUID();
    Contact contact =
        Contact.reconstitute(
            id,
            UUID.randomUUID(),
            "Carol",
            "Gomez",
            "caro@test.com",
            null,
            Map.of(),
            Set.of(),
            OffsetDateTime.now());

    // Act
    ContactSummaryResult result = ContactSummaryResult.from(contact);

    // Assert
    assertThat(result.id()).isEqualTo(id);
    assertThat(result.firstName()).isEqualTo("Carol");
    assertThat(result.lastName()).isEqualTo("Gomez");
    assertThat(result.primaryEmail()).isEqualTo("caro@test.com");
  }

  private Contact createContactWithTags(Set<UUID> tags) {
    return Contact.reconstitute(
        UUID.randomUUID(),
        UUID.randomUUID(),
        "Test",
        "User",
        "test@test.com",
        null,
        Map.of(),
        tags,
        OffsetDateTime.now());
  }
}
