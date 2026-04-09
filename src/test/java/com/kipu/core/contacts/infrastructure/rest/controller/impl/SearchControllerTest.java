package com.kipu.core.contacts.infrastructure.rest.controller.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.application.search.GetGlobalSearchUseCase;
import com.kipu.core.contacts.application.search.GlobalSearchResult;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class SearchControllerTest {

  @Mock private GetGlobalSearchUseCase getGlobalSearchUseCase;

  @InjectMocks private SearchController searchController;

  @Test
  @DisplayName("search: Should return 200 OK and search results when query is provided")
  void search_ReturnsOkAndResults_WhenQueryIsProvided() {
    // Arrange
    UUID userId = UUID.randomUUID();
    String query = "pasta";

    GlobalSearchResult result1 =
        new GlobalSearchResult(
            UUID.randomUUID(),
            "EVENT",
            "Pasta con Maria",
            "25/10/2026 20:00",
            new GlobalSearchResult.TagInfo("amor", "#00FFF8"));

    GlobalSearchResult result2 =
        new GlobalSearchResult(
            UUID.randomUUID(), "CONTACT", "Julian Pasaporte", "julian@kipu.com", null);

    List<GlobalSearchResult> expectedResults = List.of(result1, result2);

    when(getGlobalSearchUseCase.execute(userId, query)).thenReturn(expectedResults);

    // Act
    ResponseEntity<List<GlobalSearchResult>> response = searchController.search(userId, query);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull().hasSize(2);
    assertThat(response.getBody()).isEqualTo(expectedResults);

    // Verificamos el contenido específico
    assertThat(response.getBody().get(0).type()).isEqualTo("EVENT");
    assertThat(response.getBody().get(1).type()).isEqualTo("CONTACT");
    assertThat(response.getBody().get(0).tag().name()).isEqualTo("amor");

    verify(getGlobalSearchUseCase).execute(userId, query);
  }

  @Test
  @DisplayName("search: Should return 200 OK and empty list when no results match the query")
  void search_ReturnsOkAndEmptyList_WhenNoResultsFound() {
    // Arrange
    UUID userId = UUID.randomUUID();
    String query = "xyzabc";

    when(getGlobalSearchUseCase.execute(userId, query)).thenReturn(List.of());

    // Act
    ResponseEntity<List<GlobalSearchResult>> response = searchController.search(userId, query);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull().isEmpty();

    verify(getGlobalSearchUseCase).execute(userId, query);
  }
}
