package com.kipu.core.contacts.application.search;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

import com.kipu.core.contacts.domain.model.SearchResultItem;
import com.kipu.core.contacts.domain.model.UserTag;
import com.kipu.core.contacts.domain.repository.SearchRepository;
import com.kipu.core.contacts.domain.repository.UserTagRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetGlobalSearchUseCaseTest {

  @Mock private SearchRepository searchRepository;
  @Mock private UserTagRepository userTagRepository;

  @InjectMocks private GetGlobalSearchUseCase useCase;

  private UUID ownerId;
  private UUID tagId;

  @BeforeEach
  void setUp() {
    ownerId = UUID.randomUUID();
    tagId = UUID.randomUUID();
  }

  @Test
  @DisplayName("Should return empty list when query is null or shorter than 3 characters")
  void shouldReturnEmptyWhenQueryIsInvalid() {
    assertThat(useCase.execute(ownerId, null)).isEmpty();
    assertThat(useCase.execute(ownerId, "  ")).isEmpty();
    assertThat(useCase.execute(ownerId, "ab")).isEmpty();

    verifyNoInteractions(searchRepository);
    verifyNoInteractions(userTagRepository);
  }

  @Test
  @DisplayName("Should return results without fetching tags if results have no tagIds")
  void shouldReturnResultsWithoutTags() {
    // GIVEN
    String query = "miranda";
    SearchResultItem item =
        new SearchResultItem(
            UUID.randomUUID(), "CONTACT", "Julian Miranda", "julian@kipu.com", 0.9, null);

    when(searchRepository.search(ownerId, query)).thenReturn(List.of(item));

    // WHEN
    List<GlobalSearchResult> results = useCase.execute(ownerId, query);

    // THEN
    assertThat(results).hasSize(1);
    assertThat(results.get(0).tag()).isNull();
    verify(userTagRepository, never()).findAllById(anySet());
  }

  @Test
  @DisplayName("Should hydrate tags correctly when search results contain tagIds")
  void shouldHydrateTagsCorrectly() {
    // GIVEN
    String query = "pasta";
    SearchResultItem item =
        new SearchResultItem(UUID.randomUUID(), "EVENT", "Pasta con Maria", "Mañana", 1.0, tagId);

    // Usamos reconstitute para simular el objeto que viene de la DB
    UserTag mockTag = UserTag.reconstitute(tagId, ownerId, "Amor", "#00FFF8");

    when(searchRepository.search(ownerId, query)).thenReturn(List.of(item));
    when(userTagRepository.findAllById(Set.of(tagId))).thenReturn(List.of(mockTag));

    // WHEN
    List<GlobalSearchResult> results = useCase.execute(ownerId, query);

    // THEN
    assertThat(results).hasSize(1);
    assertThat(results.get(0).tag()).isNotNull();
    assertThat(results.get(0).tag().name()).isEqualTo("amor"); // Ojo: tu modelo hace toLowerCase()
    assertThat(results.get(0).tag().colorHex()).isEqualTo("#00FFF8");

    verify(userTagRepository).findAllById(Set.of(tagId));
  }

  @Test
  @DisplayName("Should handle cases where tag is missing in repository (defensive mapping)")
  void shouldHandleMissingTagInRepository() {
    // GIVEN
    String query = "test";
    SearchResultItem item =
        new SearchResultItem(UUID.randomUUID(), "CONTACT", "Title", "Sub", 1.0, tagId);

    when(searchRepository.search(ownerId, query)).thenReturn(List.of(item));
    when(userTagRepository.findAllById(Set.of(tagId)))
        .thenReturn(List.of()); // Repo no lo encuentra

    // WHEN
    List<GlobalSearchResult> results = useCase.execute(ownerId, query);

    // THEN
    assertThat(results.get(0).tag()).isNull();
  }

  @Test
  @DisplayName("Should return empty list and not fetch tags when search returns no items")
  void shouldReturnEmptyAndNotFetchTagsWhenNoSearchItemsFound() {
    // GIVEN
    String query = "nonexistent";
    when(searchRepository.search(ownerId, query)).thenReturn(List.of());

    // WHEN
    List<GlobalSearchResult> results = useCase.execute(ownerId, query);

    // THEN
    assertThat(results).isEmpty();

    // Verificamos que la búsqueda se hizo...
    verify(searchRepository).search(ownerId, query);

    // ...pero que NUNCA se intentó llamar al repositorio de tags
    verifyNoInteractions(userTagRepository);
  }
}
