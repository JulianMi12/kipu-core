package com.kipu.core.contacts.infrastructure.persistence.jpa.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.domain.model.SearchResultItem;
import com.kipu.core.contacts.infrastructure.persistence.jpa.projection.SearchResultProjection;
import com.kipu.core.contacts.infrastructure.persistence.jpa.repository.JpaSearchRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JpaSearchRepositoryAdapterTest {

  @Mock private JpaSearchRepository jpaSearchRepository;

  @InjectMocks private JpaSearchRepositoryAdapter adapter;

  @Test
  @DisplayName("Should map projection results to domain search items correctly")
  void shouldMapProjectionToDomainItem() {
    // GIVEN
    UUID ownerId = UUID.randomUUID();
    String query = "julian";
    UUID contactId = UUID.randomUUID();
    UUID tagId = UUID.randomUUID();

    SearchResultProjection mockProjection = mock(SearchResultProjection.class);
    when(mockProjection.getId()).thenReturn(contactId);
    when(mockProjection.getType()).thenReturn("CONTACT");
    when(mockProjection.getTitle()).thenReturn("Julian Miranda");
    when(mockProjection.getSubtitle()).thenReturn("julian@kipu.com");
    when(mockProjection.getScore()).thenReturn(0.95);
    when(mockProjection.getTagId()).thenReturn(tagId);

    when(jpaSearchRepository.findGlobalSearchResults(ownerId, query))
        .thenReturn(List.of(mockProjection));

    // WHEN
    List<SearchResultItem> results = adapter.search(ownerId, query);

    // THEN
    assertThat(results).hasSize(1);
    SearchResultItem item = results.get(0);

    assertThat(item.id()).isEqualTo(contactId);
    assertThat(item.type()).isEqualTo("CONTACT");
    assertThat(item.title()).isEqualTo("Julian Miranda");
    assertThat(item.subtitle()).isEqualTo("julian@kipu.com");
    assertThat(item.score()).isEqualTo(0.95);
    assertThat(item.tagId()).isEqualTo(tagId);

    verify(jpaSearchRepository).findGlobalSearchResults(ownerId, query);
  }

  @Test
  @DisplayName("Should return empty list when repository returns no results")
  void shouldReturnEmptyListWhenNoResults() {
    // GIVEN
    UUID ownerId = UUID.randomUUID();
    String query = "empty";
    when(jpaSearchRepository.findGlobalSearchResults(ownerId, query)).thenReturn(List.of());

    // WHEN
    List<SearchResultItem> results = adapter.search(ownerId, query);

    // THEN
    assertThat(results).isEmpty();
    verify(jpaSearchRepository).findGlobalSearchResults(ownerId, query);
  }
}
