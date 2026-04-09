package com.kipu.core.contacts.infrastructure.persistence.jpa.adapter;

import com.kipu.core.contacts.domain.model.SearchResultItem;
import com.kipu.core.contacts.domain.repository.SearchRepository;
import com.kipu.core.contacts.infrastructure.persistence.jpa.repository.JpaSearchRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaSearchRepositoryAdapter implements SearchRepository {

  private final JpaSearchRepository jpaSearchRepository;

  @Override
  public List<SearchResultItem> search(UUID ownerUserId, String query) {
    return jpaSearchRepository.findGlobalSearchResults(ownerUserId, query).stream()
        .map(
            p ->
                new SearchResultItem(
                    p.getId(),
                    p.getType(),
                    p.getTitle(),
                    p.getSubtitle(),
                    p.getScore(),
                    p.getTagId()))
        .toList();
  }
}
