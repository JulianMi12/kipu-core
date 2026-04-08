package com.kipu.core.contacts.infrastructure.rest.controller;

import com.kipu.core.contacts.application.tag.create.CreateUserTagResult;
import com.kipu.core.contacts.application.tag.get.UserTagResult;
import com.kipu.core.contacts.application.tag.update.UpdateUserTagResult;
import com.kipu.core.contacts.infrastructure.rest.dto.CreateUserTagRequest;
import com.kipu.core.contacts.infrastructure.rest.dto.UpdateUserTagRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "User Tags", description = "Manage tags for labeling contacts and events")
@SecurityRequirement(name = "bearerAuth")
public interface UserTagApi {

  @Operation(summary = "Create a new tag")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Tag created successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid request body"),
    @ApiResponse(responseCode = "401", description = "Missing or invalid access token")
  })
  ResponseEntity<CreateUserTagResult> createTag(
      @AuthenticationPrincipal UUID userId, @Valid @RequestBody CreateUserTagRequest request);

  @Operation(summary = "List all tags for the authenticated user")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Tags retrieved successfully"),
    @ApiResponse(responseCode = "401", description = "Missing or invalid access token")
  })
  ResponseEntity<List<UserTagResult>> getTags(@AuthenticationPrincipal UUID userId);

  @Operation(summary = "Update an existing tag")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Tag updated successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid request body"),
    @ApiResponse(responseCode = "401", description = "Missing or invalid access token"),
    @ApiResponse(responseCode = "403", description = "Access denied to this tag"),
    @ApiResponse(responseCode = "404", description = "Tag not found")
  })
  ResponseEntity<UpdateUserTagResult> updateTag(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID tagId,
      @Valid @RequestBody UpdateUserTagRequest request);

  @Operation(summary = "Delete a tag")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Tag deleted successfully"),
    @ApiResponse(responseCode = "401", description = "Missing or invalid access token"),
    @ApiResponse(responseCode = "403", description = "Access denied to this tag"),
    @ApiResponse(responseCode = "404", description = "Tag not found")
  })
  ResponseEntity<Void> deleteTag(@AuthenticationPrincipal UUID userId, @PathVariable UUID tagId);
}
