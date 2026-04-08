package com.kipu.core.contacts.infrastructure.rest.controller.impl;

import com.kipu.core.contacts.application.tag.create.CreateUserTagCommand;
import com.kipu.core.contacts.application.tag.create.CreateUserTagResult;
import com.kipu.core.contacts.application.tag.create.CreateUserTagUseCase;
import com.kipu.core.contacts.application.tag.delete.DeleteUserTagUseCase;
import com.kipu.core.contacts.application.tag.get.GetUserTagsUseCase;
import com.kipu.core.contacts.application.tag.get.UserTagResult;
import com.kipu.core.contacts.application.tag.update.UpdateUserTagCommand;
import com.kipu.core.contacts.application.tag.update.UpdateUserTagResult;
import com.kipu.core.contacts.application.tag.update.UpdateUserTagUseCase;
import com.kipu.core.contacts.infrastructure.rest.controller.UserTagApi;
import com.kipu.core.contacts.infrastructure.rest.dto.CreateUserTagRequest;
import com.kipu.core.contacts.infrastructure.rest.dto.UpdateUserTagRequest;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class UserTagController implements UserTagApi {

  private final CreateUserTagUseCase createUserTagUseCase;
  private final DeleteUserTagUseCase deleteUserTagUseCase;
  private final UpdateUserTagUseCase updateUserTagUseCase;
  private final GetUserTagsUseCase getUserTagsUseCase;

  @Override
  @PostMapping
  public ResponseEntity<CreateUserTagResult> createTag(
      @AuthenticationPrincipal UUID userId, @RequestBody CreateUserTagRequest request) {
    log.info("[UserTagController] POST /tags called for user id: {}", userId);
    CreateUserTagResult result =
        createUserTagUseCase.execute(
            new CreateUserTagCommand(userId, request.name(), request.colorHex()));
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }

  @Override
  @GetMapping
  public ResponseEntity<List<UserTagResult>> getTags(@AuthenticationPrincipal UUID userId) {
    log.info("[UserTagController] GET /tags called for user id: {}", userId);
    return ResponseEntity.ok(getUserTagsUseCase.execute(userId));
  }

  @Override
  @PutMapping("/{tagId}")
  public ResponseEntity<UpdateUserTagResult> updateTag(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID tagId,
      @RequestBody UpdateUserTagRequest request) {
    log.info("[UserTagController] PUT /tags/{} called for user id: {}", tagId, userId);
    UpdateUserTagResult result =
        updateUserTagUseCase.execute(
            new UpdateUserTagCommand(userId, tagId, request.name(), request.colorHex()));
    return ResponseEntity.ok(result);
  }

  @Override
  @DeleteMapping("/{tagId}")
  public ResponseEntity<Void> deleteTag(
      @AuthenticationPrincipal UUID userId, @PathVariable UUID tagId) {
    log.info("[UserTagController] DELETE /tags/{} called for user id: {}", tagId, userId);
    deleteUserTagUseCase.execute(userId, tagId);
    return ResponseEntity.noContent().build();
  }
}
