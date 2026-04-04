package com.kipu.core.identity.infrastructure.rest.controller;

import com.kipu.core.identity.infrastructure.rest.dto.LoginRequest;
import com.kipu.core.identity.infrastructure.rest.dto.LoginResponse;
import com.kipu.core.identity.infrastructure.rest.dto.RefreshRequest;
import com.kipu.core.identity.infrastructure.rest.dto.RefreshResponse;
import com.kipu.core.identity.infrastructure.rest.dto.RegisterUserRequest;
import com.kipu.core.identity.infrastructure.rest.dto.RegistrationResponse;
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

@Tag(name = "Auth", description = "Operations related to identity management")
public interface AuthApi {

  @Operation(
      summary = "Register and Auto-login",
      description = "Creates a new user and returns authentication tokens for immediate access.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "User created and authenticated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Email already exists")
      })
  ResponseEntity<RegistrationResponse> registerUser(
      @Valid @RequestBody RegisterUserRequest request);

  @Operation(
      summary = "Login",
      description = "Authenticates a user and returns a fresh pair of JWT tokens.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Authentication successful"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
      })
  ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request);

  @Operation(
      summary = "Refresh tokens",
      description = "Validates a refresh token, rotates it, and issues a new token pair.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Tokens refreshed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
      })
  ResponseEntity<RefreshResponse> refresh(@Valid @RequestBody RefreshRequest request);

  @Operation(
      summary = "Logout",
      description = "Invalidates the current session by deleting the user's refresh token.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Logged out successfully"),
        @ApiResponse(responseCode = "401", description = "Missing or invalid access token")
      })
  ResponseEntity<Void> logout(@AuthenticationPrincipal UUID userId);
}
