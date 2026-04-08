package com.kipu.core.contacts.application.tag.update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.domain.exception.TagAlreadyExistsException;
import com.kipu.core.contacts.domain.exception.UnauthorizedContactAccessException;
import com.kipu.core.contacts.domain.exception.UserTagNotFoundException;
import com.kipu.core.contacts.domain.model.UserTag;
import com.kipu.core.contacts.domain.repository.UserTagRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateUserTagUseCaseTest {

  @Mock private UserTagRepository userTagRepository;
  @InjectMocks private UpdateUserTagUseCase updateUserTagUseCase;

  private UUID tagId;
  private UUID userId;

  @BeforeEach
  void setUp() {
    tagId = UUID.randomUUID();
    userId = UUID.randomUUID();
  }

  @Test
  @DisplayName("Exito: Actualizar nombre y color")
  void execute_Success() {
    // Arrange
    UpdateUserTagCommand command = new UpdateUserTagCommand(userId, tagId, " NUEVO ", "#ABC");
    UserTag tagEnDb = UserTag.reconstitute(tagId, userId, "viejo", "#000");

    when(userTagRepository.findById(tagId)).thenReturn(Optional.of(tagEnDb));
    when(userTagRepository.existsByNameAndOwnerUserId("nuevo", userId)).thenReturn(false);
    when(userTagRepository.save(any(UserTag.class))).thenAnswer(i -> i.getArgument(0));

    // Act
    UpdateUserTagResult result = updateUserTagUseCase.execute(command);

    // Assert
    assertEquals("nuevo", result.name());
    assertEquals("#ABC", result.colorHex());
    verify(userTagRepository).save(tagEnDb);
  }

  @Test
  @DisplayName("Exito: No validar si el nombre no cambió")
  void execute_Success_NoNameChange() {
    // Arrange
    UpdateUserTagCommand command = new UpdateUserTagCommand(userId, tagId, "mismo", "#FFF");
    UserTag tagEnDb = UserTag.reconstitute(tagId, userId, "mismo", "#000");

    when(userTagRepository.findById(tagId)).thenReturn(Optional.of(tagEnDb));
    when(userTagRepository.save(any(UserTag.class))).thenAnswer(i -> i.getArgument(0));

    // Act
    updateUserTagUseCase.execute(command);

    // Assert
    // No debe llamar a existsByName porque el nombre es igual
    verify(userTagRepository, never()).existsByNameAndOwnerUserId(anyString(), any());
    verify(userTagRepository).save(tagEnDb);
  }

  @Test
  @DisplayName("Error: Tag no encontrado")
  void execute_NotFound() {
    // Arrange
    UpdateUserTagCommand command = new UpdateUserTagCommand(userId, tagId, "Any", "#000");
    when(userTagRepository.findById(tagId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(UserTagNotFoundException.class, () -> updateUserTagUseCase.execute(command));
  }

  @Test
  @DisplayName("Error: Usuario no autorizado")
  void execute_Unauthorized() {
    // Arrange
    UUID diferenteUsuario = UUID.randomUUID();
    UpdateUserTagCommand command =
        new UpdateUserTagCommand(diferenteUsuario, tagId, "Hack", "#000");
    UserTag tagEnDb = UserTag.reconstitute(tagId, userId, "original", "#000");

    when(userTagRepository.findById(tagId)).thenReturn(Optional.of(tagEnDb));

    // Act & Assert
    assertThrows(
        UnauthorizedContactAccessException.class, () -> updateUserTagUseCase.execute(command));
  }

  @Test
  @DisplayName("Error: Nombre ya existe para el mismo usuario")
  void execute_NameConflict() {
    // Arrange
    UpdateUserTagCommand command = new UpdateUserTagCommand(userId, tagId, "DUPLICADO", "#000");
    UserTag tagEnDb = UserTag.reconstitute(tagId, userId, "original", "#000");

    when(userTagRepository.findById(tagId)).thenReturn(Optional.of(tagEnDb));
    when(userTagRepository.existsByNameAndOwnerUserId("duplicado", userId)).thenReturn(true);

    // Act & Assert
    assertThrows(TagAlreadyExistsException.class, () -> updateUserTagUseCase.execute(command));
  }
}
