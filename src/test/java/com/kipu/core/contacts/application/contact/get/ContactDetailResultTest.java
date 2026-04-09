package com.kipu.core.contacts.application.contact.get;

import static org.assertj.core.api.Assertions.assertThat;

import com.kipu.core.contacts.domain.model.Contact;
import java.time.LocalDate;
import java.time.OffsetDateTime;
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
    // Calculamos una fecha de hace 20 años exactos
    LocalDate birthdate = LocalDate.now().minusYears(20);
    Contact contact = createTestContact(birthdate);

    // Act
    ContactDetailResult result = ContactDetailResult.from(contact);

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
    ContactDetailResult result = ContactDetailResult.from(contact);

    // Assert
    assertThat(result.age()).isNull();
    assertThat(result.birthdate()).isNull();
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
    ContactDetailResult result = ContactDetailResult.from(contact);

    // Assert
    assertThat(result.id()).isEqualTo(id);
    assertThat(result.firstName()).isEqualTo("Julian");
    assertThat(result.lastName()).isEqualTo("Miranda");
    assertThat(result.primaryEmail()).isEqualTo("juli@test.com");
    assertThat(result.dynamicAttributes()).isEqualTo(attrs);
    assertThat(result.tagIds()).containsAll(tags);
    // Verificamos que la edad sea coherente (ej. en 2026 para alguien nacido en 1990)
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
