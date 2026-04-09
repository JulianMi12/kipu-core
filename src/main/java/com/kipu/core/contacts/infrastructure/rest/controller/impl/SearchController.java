package com.kipu.core.contacts.infrastructure.rest.controller.impl;

import com.kipu.core.contacts.application.search.GetGlobalSearchUseCase;
import com.kipu.core.contacts.application.search.GlobalSearchResult;
import com.kipu.core.contacts.infrastructure.rest.controller.SearchApi;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController implements SearchApi {

  private final GetGlobalSearchUseCase getGlobalSearchUseCase;

  @Override
  @GetMapping
  public ResponseEntity<List<GlobalSearchResult>> search(
      @AuthenticationPrincipal UUID userId, @RequestParam String query) {
    log.info("[SearchController] GET /search called for userId: {} with query: {}", userId, query);
    return ResponseEntity.ok(getGlobalSearchUseCase.execute(userId, query));
  }
}
