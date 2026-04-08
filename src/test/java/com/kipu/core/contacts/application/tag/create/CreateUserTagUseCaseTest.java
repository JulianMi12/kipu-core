package com.kipu.core.contacts.application.tag.create;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.domain.exception.TagAlreadyExistsException;
import com.kipu.core.contacts.domain.model.UserTag;
import com.kipu.core.contacts.domain.repository.UserTagRepository;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateUserTagUseCaseTest {

  @Mock private UserTagRepository userTagRepository;

  @InjectMocks private CreateUserTagUseCase createUserTagUseCase;

  @Test
  @DisplayName("execute: Should create and save tag when name does not exist")
  void execute_ShouldCreateAndSaveTag_WhenNameIsUnique() {
    // Arrange
    UUID userId = UUID.randomUUID();
    String inputName = "  PERSONAL  ";
    String normalizedName = "personal";
    String color = "#4F46E5";
    CreateUserTagCommand command = new CreateUserTagCommand(userId, inputName, color);

    // Simulamos que el tag NO existe
    when(userTagRepository.existsByNameAndOwnerUserId(normalizedName, userId)).thenReturn(false);

    // Simulamos el guardado (UserTag.create se encarga de la lógica interna)
    UserTag mockSavedTag = UserTag.create(userId, normalizedName, color);
    when(userTagRepository.save(any(UserTag.class))).thenReturn(mockSavedTag);

    // Act
    CreateUserTagResult result = createUserTagUseCase.execute(command);

    // Assert
    assertNotNull(result);
    assertEquals(normalizedName, result.name());

    // Verificamos que se llamó al repositorio con el nombre normalizado
    verify(userTagRepository).existsByNameAndOwnerUserId(normalizedName, userId);

    // Capturamos el tag que se intentó guardar para asegurar que se normalizó
    ArgumentCaptor<UserTag> tagCaptor = ArgumentCaptor.forClass(UserTag.class);
    verify(userTagRepository).save(tagCaptor.capture());
    assertEquals(normalizedName, tagCaptor.getValue().getName());
  }

  @Test
  @DisplayName("execute: Should throw TagAlreadyExistsException when name already exists")
  void execute_ShouldThrowException_WhenTagExists() {
    // Arrange
    UUID userId = UUID.randomUUID();
    String normalizedName = "documentos";
    CreateUserTagCommand command = new CreateUserTagCommand(userId, " Documentos ", "#000000");

    // Simulamos que el tag YA existe
    when(userTagRepository.existsByNameAndOwnerUserId(normalizedName, userId)).thenReturn(true);

    // Act & Assert
    TagAlreadyExistsException exception =
        assertThrows(TagAlreadyExistsException.class, () -> createUserTagUseCase.execute(command));

    assertEquals(
        "Tag with name 'documentos' already exists for this user.", exception.getMessage());
    verify(userTagRepository, never()).save(any(UserTag.class));
  }
}
