package com.kipu.core.contacts.infrastructure.persistence.jpa.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter
public class DynamicAttributesConverter implements AttributeConverter<Map<String, Object>, String> {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final TypeReference<Map<String, Object>> TYPE_REF = new TypeReference<>() {};

  @Override
  public String convertToDatabaseColumn(Map<String, Object> attribute) {
    if (attribute == null || attribute.isEmpty()) {
      return "{}";
    }
    try {
      return OBJECT_MAPPER.writeValueAsString(attribute);
    } catch (JsonProcessingException e) {
      log.error("[DynamicAttributesConverter] Failed to serialize dynamic attributes", e);
      return "{}";
    }
  }

  @Override
  public Map<String, Object> convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isBlank()) {
      return Map.of();
    }
    try {
      return OBJECT_MAPPER.readValue(dbData, TYPE_REF);
    } catch (JsonProcessingException e) {
      log.error("[DynamicAttributesConverter] Failed to deserialize dynamic attributes", e);
      return Map.of();
    }
  }
}
