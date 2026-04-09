package com.kipu.core.contacts.infrastructure.persistence.jpa.projection;

import java.util.UUID;

public interface SearchResultProjection {
  UUID getId();

  String getType();

  String getTitle();

  String getSubtitle();

  Double getScore();

  UUID getTagId();
}
