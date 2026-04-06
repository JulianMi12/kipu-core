package com.kipu.core.identity.infrastructure.rest.controller;

import com.kipu.core.identity.infrastructure.rest.dto.OnboardingRequest;
import com.kipu.core.identity.infrastructure.rest.dto.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Onboarding", description = "Operations related to user onboarding")
public interface OnboardingApi {

  @Operation(
      summary = "Complete user onboarding",
      description = "Submits basic KYC information to complete the onboarding process.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Onboarding completed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request body"),
        @ApiResponse(responseCode = "401", description = "Missing or invalid access token"),
        @ApiResponse(responseCode = "404", description = "User not found")
      })
  ResponseEntity<UserProfileResponse> completeOnboarding(
      @AuthenticationPrincipal UUID userId, @Valid @RequestBody OnboardingRequest request);
}
