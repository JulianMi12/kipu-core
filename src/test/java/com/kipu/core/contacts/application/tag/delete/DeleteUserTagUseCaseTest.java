package com.kipu.core.contacts.application.tag.delete;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.domain.exception.UnauthorizedContactAccessException;
import com.kipu.core.contacts.domain.exception.UserTagNotFoundException;
import com.kipu.core.contacts.domain.model.UserTag;
import com.kipu.core.contacts.domain.repository.UserTagRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteUserTagUseCaseTest {

  @Mock private UserTagRepository userTagRepository;

  @InjectMocks private DeleteUserTagUseCase deleteUserTagUseCase;

  @Test
  @DisplayName("execute: Should delete tag when user is the owner")
  void execute_ShouldDelete_WhenUserIsOwner() {
    // Arrange
    UUID tagId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    UserTag mockTag = mock(UserTag.class);

    when(userTagRepository.findById(tagId)).thenReturn(Optional.of(mockTag));
    when(mockTag.getOwnerUserId()).thenReturn(userId);

    // Act
    deleteUserTagUseCase.execute(userId, tagId);

    // Assert
    verify(userTagRepository).findById(tagId);
    verify(userTagRepository).delete(tagId);
  }

  @Test
  @DisplayName("execute: Should throw UserTagNotFoundException when tag does not exist")
  void execute_ShouldThrowNotFound_WhenTagDoesNotExist() {
    // Arrange
    UUID tagId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    when(userTagRepository.findById(tagId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(UserTagNotFoundException.class, () -> deleteUserTagUseCase.execute(userId, tagId));

    verify(userTagRepository, never()).delete(tagId);
  }

  @Test
  @DisplayName(
      "execute: Should throw UnauthorizedContactAccessException when user is not the owner")
  void execute_ShouldThrowUnauthorized_WhenUserIsNotOwner() {
    // Arrange
    UUID tagId = UUID.randomUUID();
    UUID ownerId = UUID.randomUUID();
    UUID intruderId = UUID.randomUUID();
    UserTag mockTag = mock(UserTag.class);

    when(userTagRepository.findById(tagId)).thenReturn(Optional.of(mockTag));
    when(mockTag.getOwnerUserId()).thenReturn(ownerId);

    // Act & Assert
    assertThrows(
        UnauthorizedContactAccessException.class,
        () -> deleteUserTagUseCase.execute(intruderId, tagId));

    verify(userTagRepository, never()).delete(tagId);
  }
}
