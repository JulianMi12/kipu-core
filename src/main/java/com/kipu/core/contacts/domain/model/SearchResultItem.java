package com.kipu.core.contacts.domain.model;

import java.util.UUID;

public record SearchResultItem(
    UUID id, String type, String title, String subtitle, double score, UUID tagId) {}
