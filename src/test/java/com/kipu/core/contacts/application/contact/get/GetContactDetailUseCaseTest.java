package com.kipu.core.contacts.application.contact.get;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.domain.exception.ContactNotFoundException;
import com.kipu.core.contacts.domain.exception.UnauthorizedContactAccessException;
import com.kipu.core.contacts.domain.model.Contact;
import com.kipu.core.contacts.domain.repository.ContactRepository;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetContactDetailUseCaseTest {

  @Mock private ContactRepository contactRepository;

  @InjectMocks private GetContactDetailUseCase getContactDetailUseCase;

  @Test
  @DisplayName("execute: Should return contact detail when contact exists and belongs to user")
  void execute_ShouldReturnDetail_WhenValid() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    Contact contact = createTestContact(contactId, userId);

    when(contactRepository.findByIdWithTags(contactId)).thenReturn(Optional.of(contact));

    // Act
    ContactDetailResult result = getContactDetailUseCase.execute(userId, contactId);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(contactId);
    verify(contactRepository).findByIdWithTags(contactId);
  }

  @Test
  @DisplayName("execute: Should throw ContactNotFoundException when contact does not exist")
  void execute_ShouldThrowNotFound_WhenIdDoesNotExist() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();

    when(contactRepository.findByIdWithTags(contactId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> getContactDetailUseCase.execute(userId, contactId))
        .isInstanceOf(ContactNotFoundException.class);
  }

  @Test
  @DisplayName(
      "execute: Should throw UnauthorizedContactAccessException when user is not the owner")
  void execute_ShouldThrowUnauthorized_WhenUserIsNotOwner() {
    // Arrange
    UUID maliciousUserId = UUID.randomUUID();
    UUID realOwnerId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    Contact contact = createTestContact(contactId, realOwnerId);

    when(contactRepository.findByIdWithTags(contactId)).thenReturn(Optional.of(contact));

    // Act & Assert
    assertThatThrownBy(() -> getContactDetailUseCase.execute(maliciousUserId, contactId))
        .isInstanceOf(UnauthorizedContactAccessException.class);
  }

  private Contact createTestContact(UUID id, UUID ownerId) {
    return Contact.reconstitute(
        id,
        ownerId,
        "Julian",
        "Miranda",
        "dev@test.com",
        null,
        Map.of(),
        Set.of(),
        OffsetDateTime.now());
  }
}
