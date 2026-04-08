package com.kipu.core.contacts.application.tag.create;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.domain.model.UserTag;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreateUserTagResultTest {

  @Test
  @DisplayName("Constructor: Should correctly assign values to fields")
  void constructor_ShouldAssignFields() {
    // Arrange
    UUID id = UUID.randomUUID();
    String name = "personal";
    String color = "#4F46E5";

    // Act
    CreateUserTagResult result = new CreateUserTagResult(id, name, color);

    // Assert
    assertEquals(id, result.tagId());
    assertEquals(name, result.name());
    assertEquals(color, result.colorHex());
  }

  @Test
  @DisplayName("from: Should map Domain UserTag to Result DTO correctly")
  void from_ShouldMapUserTagToResult() {
    // Arrange
    UUID id = UUID.randomUUID();
    String name = "documentos";
    String color = "#00FFF8";

    // Usamos Mock para evitar lógica interna de la entidad de dominio
    UserTag mockTag = mock(UserTag.class);
    when(mockTag.getId()).thenReturn(id);
    when(mockTag.getName()).thenReturn(name);
    when(mockTag.getColorHex()).thenReturn(color);

    // Act
    CreateUserTagResult result = CreateUserTagResult.from(mockTag);

    // Assert
    assertNotNull(result);
    assertEquals(id, result.tagId());
    assertEquals(name, result.name());
    assertEquals(color, result.colorHex());
  }
}
