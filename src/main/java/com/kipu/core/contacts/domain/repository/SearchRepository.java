package com.kipu.core.contacts.domain.repository;

import com.kipu.core.contacts.domain.model.SearchResultItem;
import java.util.List;
import java.util.UUID;

public interface SearchRepository {

  List<SearchResultItem> search(UUID ownerUserId, String query);
}
