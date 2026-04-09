package com.kipu.core.contacts.infrastructure.rest.controller;

import com.kipu.core.contacts.application.search.GlobalSearchResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Search", description = "Global search across contacts and events")
@SecurityRequirement(name = "bearerAuth")
public interface SearchApi {

  @Operation(
      summary = "Global search",
      description =
          "Searches contacts (by name and email) and events (by title) using fuzzy matching. "
              + "Returns up to 15 results ordered by relevance score. Minimum query length: 2 characters.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Search results returned successfully"),
    @ApiResponse(responseCode = "401", description = "Missing or invalid access token")
  })
  ResponseEntity<List<GlobalSearchResult>> search(
      @AuthenticationPrincipal UUID userId, @RequestParam String query);
}
