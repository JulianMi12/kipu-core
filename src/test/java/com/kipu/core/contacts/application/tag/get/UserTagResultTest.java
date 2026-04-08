package com.kipu.core.contacts.application.tag.get;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.domain.model.UserTag;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTagResultTest {

  @Test
  @DisplayName("Constructor: Should correctly assign values to record components")
  void constructor_ShouldAssignFields() {
    // Arrange
    UUID id = UUID.randomUUID();
    String name = "personal";
    String color = "#4F46E5";

    // Act
    UserTagResult result = new UserTagResult(id, name, color);

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
    String name = "trabajo";
    String color = "#000000";

    // Usamos Mock para aislar la prueba del DTO de la lógica de la entidad de dominio
    UserTag mockTag = mock(UserTag.class);
    when(mockTag.getId()).thenReturn(id);
    when(mockTag.getName()).thenReturn(name);
    when(mockTag.getColorHex()).thenReturn(color);

    // Act
    UserTagResult result = UserTagResult.from(mockTag);

    // Assert
    assertNotNull(result);
    assertEquals(id, result.tagId());
    assertEquals(name, result.name());
    assertEquals(color, result.colorHex());
  }
}
