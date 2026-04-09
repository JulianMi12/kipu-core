package com.kipu.core.contacts.infrastructure.rest.controller;

import com.kipu.core.contacts.application.event.query.UpcomingEventResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Tag(name = "Contact Event Queries", description = "Query endpoints for contact events")
@SecurityRequirement(name = "bearerAuth")
public interface ContactEventQueryApi {

  @Operation(summary = "Get upcoming pending events for the authenticated user (Your Day)")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Upcoming events retrieved successfully"),
    @ApiResponse(responseCode = "401", description = "Missing or invalid access token")
  })
  ResponseEntity<List<UpcomingEventResult>> getUpcomingEvents(@AuthenticationPrincipal UUID userId);
}
