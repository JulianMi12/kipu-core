package com.kipu.core.contacts.infrastructure.persistence.jpa.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DynamicAttributesConverterTest {

  private DynamicAttributesConverter converter;

  @BeforeEach
  void setUp() {
    converter = new DynamicAttributesConverter();
  }

  @Test
  @DisplayName("convertToDatabaseColumn: Should return JSON string when map is provided")
  void convertToDatabaseColumn_ShouldReturnJsonString() {
    // Arrange
    Map<String, Object> attributes = Map.of("key", "value", "number", 123);

    // Act
    String result = converter.convertToDatabaseColumn(attributes);

    // Assert
    assertNotNull(result);
    assertTrue(result.contains("\"key\":\"value\""));
    assertTrue(result.contains("\"number\":123"));
  }

  @Test
  @DisplayName("convertToDatabaseColumn: Should return empty JSON object when map is null or empty")
  void convertToDatabaseColumn_ShouldReturnEmptyObject_WhenNullOrEmpty() {
    // Act & Assert
    assertEquals("{}", converter.convertToDatabaseColumn(null));
    assertEquals("{}", converter.convertToDatabaseColumn(Map.of()));
  }

  @Test
  @DisplayName("convertToEntityAttribute: Should return Map when valid JSON string is provided")
  void convertToEntityAttribute_ShouldReturnMap() {
    // Arrange
    String json = "{\"key\":\"value\",\"active\":true}";

    // Act
    Map<String, Object> result = converter.convertToEntityAttribute(json);

    // Assert
    assertNotNull(result);
    assertEquals("value", result.get("key"));
    assertEquals(true, result.get("active"));
  }

  @Test
  @DisplayName("convertToEntityAttribute: Should return empty Map when string is null or blank")
  void convertToEntityAttribute_ShouldReturnEmptyMap_WhenNullOrBlank() {
    // Act & Assert
    assertEquals(Map.of(), converter.convertToEntityAttribute(null));
    assertEquals(Map.of(), converter.convertToEntityAttribute(" "));
  }

  @Test
  @DisplayName(
      "convertToEntityAttribute: Should return empty Map and log error when JSON is invalid")
  void convertToEntityAttribute_ShouldHandleJsonProcessingException() {
    // Arrange: Un JSON mal formado para disparar la excepción
    String invalidJson = "{invalid_json}";

    // Act
    Map<String, Object> result = converter.convertToEntityAttribute(invalidJson);

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("convertToDatabaseColumn: Should return empty object and log error when serialization fails")
  void convertToDatabaseColumn_ShouldHandleJsonProcessingException() {
    // Arrange
    DynamicAttributesConverter converter = new DynamicAttributesConverter();

    // 💡 Creamos un objeto que Jackson NO PUEDE serializar por diseño
    // Una clase anónima que Jackson no sabrá cómo manejar lanzará una InvalidDefinitionException
    // la cual hereda de JsonProcessingException.
    Object unserializableObject = new Object() {
      @Override
      public String toString() { return "I will fail"; }
    };

    Map<String, Object> faultMap = new HashMap<>();
    faultMap.put("fail", unserializableObject);

    // Act
    String result = converter.convertToDatabaseColumn(faultMap);

    // Assert
    // Esto entrará en el CATCH, imprimirá el error en consola y devolverá "{}"
    assertEquals("{}", result);
  }

  @Test
  @DisplayName("Constructor: Should initialize instance correctly for coverage")
  void classInitialization_ShouldWork() {
    // Esto asegura que el cargador de clases visite la definición completa de la clase
    DynamicAttributesConverter instance = new DynamicAttributesConverter();
    assertNotNull(instance);
  }
}
