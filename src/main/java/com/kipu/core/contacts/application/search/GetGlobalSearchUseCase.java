package com.kipu.core.contacts.application.search;

import com.kipu.core.contacts.domain.model.SearchResultItem;
import com.kipu.core.contacts.domain.model.UserTag;
import com.kipu.core.contacts.domain.repository.SearchRepository;
import com.kipu.core.contacts.domain.repository.UserTagRepository;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetGlobalSearchUseCase {

  private static final int MIN_QUERY_LENGTH = 3;

  private final SearchRepository searchRepository;
  private final UserTagRepository userTagRepository;

  public List<GlobalSearchResult> execute(UUID ownerUserId, String query) {
    if (query == null || query.trim().length() < MIN_QUERY_LENGTH) return List.of();

    String normalizedQuery = query.trim();
    var searchItems = searchRepository.search(ownerUserId, normalizedQuery);

    if (searchItems.isEmpty()) return List.of();

    var neededTagIds =
        searchItems.stream()
            .map(SearchResultItem::tagId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

    Map<UUID, UserTag> tagMap = Map.of();
    if (!neededTagIds.isEmpty()) {
      tagMap =
          userTagRepository.findAllById(neededTagIds).stream()
              .collect(Collectors.toMap(UserTag::getId, Function.identity()));
    }

    final Map<UUID, UserTag> finalTagMap = tagMap;
    return searchItems.stream().map(item -> GlobalSearchResult.from(item, finalTagMap)).toList();
  }
}
