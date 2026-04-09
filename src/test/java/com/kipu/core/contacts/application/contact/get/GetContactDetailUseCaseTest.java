package com.kipu.core.contacts.application.contact.get;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.kipu.core.contacts.domain.exception.ContactNotFoundException;
import com.kipu.core.contacts.domain.exception.UnauthorizedContactAccessException;
import com.kipu.core.contacts.domain.model.Contact;
import com.kipu.core.contacts.domain.model.UserTag;
import com.kipu.core.contacts.domain.repository.ContactRepository;
import com.kipu.core.contacts.domain.repository.UserTagRepository;
import java.time.OffsetDateTime;
import java.util.List;
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
  @Mock private UserTagRepository userTagRepository;

  @InjectMocks private GetContactDetailUseCase getContactDetailUseCase;

  @Test
  @DisplayName("execute: Should return contact detail with hydrated tags when valid")
  void execute_ShouldReturnDetailWithTags_WhenValid() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    UUID tagId = UUID.randomUUID();

    // Contacto con un tagId
    Contact contact = createTestContact(contactId, userId, Set.of(tagId));
    // Tag correspondiente en el dominio
    UserTag tag = UserTag.reconstitute(tagId, userId, "Personal", "#4F46E5");

    when(contactRepository.findByIdWithTags(contactId)).thenReturn(Optional.of(contact));
    when(userTagRepository.findAllById(any())).thenReturn(List.of(tag));

    // Act
    ContactDetailResult result = getContactDetailUseCase.execute(userId, contactId);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(contactId);
    assertThat(result.tags()).hasSize(1);
    assertThat(result.tags().get(0).name()).isEqualTo("personal");

    verify(contactRepository).findByIdWithTags(contactId);
    verify(userTagRepository).findAllById(contact.getTagIds());
  }

  @Test
  @DisplayName("execute: Should not call userTagRepository when contact has no tags")
  void execute_ShouldNotFetchTags_WhenContactHasNoTags() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    Contact contact = createTestContact(contactId, userId, Set.of());

    when(contactRepository.findByIdWithTags(contactId)).thenReturn(Optional.of(contact));

    // Act
    getContactDetailUseCase.execute(userId, contactId);

    // Assert
    verify(userTagRepository, never()).findAllById(any());
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

    verifyNoInteractions(userTagRepository);
  }

  @Test
  @DisplayName(
      "execute: Should throw UnauthorizedContactAccessException when user is not the owner")
  void execute_ShouldThrowUnauthorized_WhenUserIsNotOwner() {
    // Arrange
    UUID maliciousUserId = UUID.randomUUID();
    UUID realOwnerId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();
    Contact contact = createTestContact(contactId, realOwnerId, Set.of());

    when(contactRepository.findByIdWithTags(contactId)).thenReturn(Optional.of(contact));

    // Act & Assert
    assertThatThrownBy(() -> getContactDetailUseCase.execute(maliciousUserId, contactId))
        .isInstanceOf(UnauthorizedContactAccessException.class);

    verifyNoInteractions(userTagRepository);
  }

  private Contact createTestContact(UUID id, UUID ownerId, Set<UUID> tagIds) {
    return Contact.reconstitute(
        id,
        ownerId,
        "Julian",
        "Miranda",
        "dev@test.com",
        null,
        Map.of(),
        tagIds,
        OffsetDateTime.now());
  }

  @Test
  @DisplayName("execute: Should handle null tagIds gracefully and not fetch tags")
  void execute_ShouldHandleNullTagIds_WhenTagsAreNullInDomain() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID contactId = UUID.randomUUID();

    // Creamos un contacto donde pasamos null explícitamente en el set de tags
    Contact contactWithNullTags =
        Contact.reconstitute(
            contactId,
            userId,
            "Julian",
            "Miranda",
            "dev@test.com",
            null,
            Map.of(),
            null, // Simulamos que el set de IDs llega nulo
            OffsetDateTime.now());

    when(contactRepository.findByIdWithTags(contactId))
        .thenReturn(Optional.of(contactWithNullTags));

    // Act
    ContactDetailResult result = getContactDetailUseCase.execute(userId, contactId);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.tags()).isEmpty();

    // Verificamos que NO se llamó al repositorio de tags
    verify(userTagRepository, never()).findAllById(any());
  }
}
