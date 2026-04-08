package com.kipu.core.contacts.application.tag.update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.domain.model.UserTag;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UpdateUserTagResultTest {

  @Test
  @DisplayName("Constructor: Should correctly assign values to fields")
  void constructor_ShouldAssignFields() {
    // Arrange
    UUID id = UUID.randomUUID();
    String name = "trabajo";
    String color = "#EF4444";

    // Act
    UpdateUserTagResult result = new UpdateUserTagResult(id, name, color);

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
    String name = "urgente";
    String color = "#F59E0B";

    // Usamos Mock para aislar la prueba del DTO de la lógica de la entidad
    UserTag mockTag = mock(UserTag.class);
    when(mockTag.getId()).thenReturn(id);
    when(mockTag.getName()).thenReturn(name);
    when(mockTag.getColorHex()).thenReturn(color);

    // Act
    UpdateUserTagResult result = UpdateUserTagResult.from(mockTag);

    // Assert
    assertNotNull(result);
    assertEquals(id, result.tagId());
    assertEquals(name, result.name());
    assertEquals(color, result.colorHex());
  }
}
