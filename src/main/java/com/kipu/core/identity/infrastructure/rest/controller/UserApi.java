package com.kipu.core.identity.infrastructure.rest.controller;

import com.kipu.core.identity.infrastructure.rest.dto.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Tag(name = "Users", description = "Operations related to user management")
public interface UserApi {

  @Operation(
      summary = "Get current user profile",
      description = "Returns the profile of the authenticated user.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Missing or invalid access token"),
        @ApiResponse(responseCode = "404", description = "User not found")
      })
  ResponseEntity<UserProfileResponse> getMyProfile(@AuthenticationPrincipal UUID userId);
}
