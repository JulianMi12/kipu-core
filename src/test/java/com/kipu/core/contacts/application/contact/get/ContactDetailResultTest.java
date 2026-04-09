package com.kipu.core.contacts.application.contact.get;

import static org.assertj.core.api.Assertions.assertThat;

import com.kipu.core.contacts.domain.model.Contact;
import com.kipu.core.contacts.domain.model.UserTag;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContactDetailResultTest {

  @Test
  @DisplayName("from: Should calculate age correctly based on birthdate")
  void from_ShouldCalculateAge_WhenBirthdateIsNotNull() {
    // Arrange
    LocalDate birthdate = LocalDate.now().minusYears(20);
    Contact contact = createTestContact(birthdate);

    // Act
    ContactDetailResult result = ContactDetailResult.from(contact, List.of());

    // Assert
    assertThat(result.age()).isEqualTo(20);
    assertThat(result.birthdate()).isEqualTo(birthdate);
  }

  @Test
  @DisplayName("from: Should return null age when birthdate is missing")
  void from_ShouldReturnNullAge_WhenBirthdateIsNull() {
    // Arrange
    Contact contact = createTestContact(null);

    // Act
    ContactDetailResult result = ContactDetailResult.from(contact, List.of());

    // Assert
    assertThat(result.age()).isNull();
    assertThat(result.birthdate()).isNull();
  }

  @Test
  @DisplayName("from: Should map tags correctly from domain UserTag to TagInfo")
  void from_ShouldMapTagsCorrectly() {
    // Arrange
    Contact contact = createTestContact(LocalDate.of(1990, 1, 1));
    UUID ownerId = contact.getOwnerUserId();

    UserTag tag1 = UserTag.reconstitute(UUID.randomUUID(), ownerId, "Personal", "#4F46E5");
    UserTag tag2 = UserTag.reconstitute(UUID.randomUUID(), ownerId, "Trabajo", "#FF5733");
    List<UserTag> domainTags = List.of(tag1, tag2);

    // Act
    ContactDetailResult result = ContactDetailResult.from(contact, domainTags);

    // Assert
    assertThat(result.tags()).hasSize(2);
    // Verificamos el primer tag (recordar que UserTag aplica toLowerCase al nombre)
    assertThat(result.tags().get(0).name()).isEqualTo("personal");
    assertThat(result.tags().get(0).colorHex()).isEqualTo("#4F46E5");
    // Verificamos el segundo tag
    assertThat(result.tags().get(1).name()).isEqualTo("trabajo");
    assertThat(result.tags().get(1).colorHex()).isEqualTo("#FF5733");
  }

  @Test
  @DisplayName("from: Should map all domain fields correctly to the record fields")
  void from_ShouldMapAllFieldsExactly() {
    // Arrange
    UUID id = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    Map<String, Object> attrs = Map.of("extra", "data");
    Set<UUID> tags = Set.of(UUID.randomUUID());
    OffsetDateTime now = OffsetDateTime.now();

    Contact contact =
        Contact.reconstitute(
            id,
            ownerId,
            "Julian",
            "Miranda",
            "juli@test.com",
            LocalDate.of(1990, 1, 1),
            attrs,
            tags,
            now);

    // Act
    ContactDetailResult result = ContactDetailResult.from(contact, List.of());

    // Assert
    assertThat(result.id()).isEqualTo(id);
    assertThat(result.firstName()).isEqualTo("Julian");
    assertThat(result.lastName()).isEqualTo("Miranda");
    assertThat(result.primaryEmail()).isEqualTo("juli@test.com");
    assertThat(result.dynamicAttributes()).isEqualTo(attrs);
    // En 2026, alguien nacido en 1990 tiene 36 años
    assertThat(result.age()).isGreaterThanOrEqualTo(36);
  }

  private Contact createTestContact(LocalDate birthdate) {
    return Contact.reconstitute(
        UUID.randomUUID(),
        UUID.randomUUID(),
        "Test",
        "User",
        "test@test.com",
        birthdate,
        Map.of(),
        Set.of(),
        OffsetDateTime.now());
  }
}
