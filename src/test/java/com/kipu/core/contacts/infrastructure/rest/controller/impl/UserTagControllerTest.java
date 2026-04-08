package com.kipu.core.contacts.infrastructure.rest.controller.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.application.tag.create.CreateUserTagCommand;
import com.kipu.core.contacts.application.tag.create.CreateUserTagResult;
import com.kipu.core.contacts.application.tag.create.CreateUserTagUseCase;
import com.kipu.core.contacts.application.tag.delete.DeleteUserTagUseCase;
import com.kipu.core.contacts.application.tag.get.GetUserTagsUseCase;
import com.kipu.core.contacts.application.tag.get.UserTagResult;
import com.kipu.core.contacts.application.tag.update.UpdateUserTagCommand;
import com.kipu.core.contacts.application.tag.update.UpdateUserTagResult;
import com.kipu.core.contacts.application.tag.update.UpdateUserTagUseCase;
import com.kipu.core.contacts.infrastructure.rest.dto.CreateUserTagRequest;
import com.kipu.core.contacts.infrastructure.rest.dto.UpdateUserTagRequest;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class UserTagControllerTest {

  @Mock private CreateUserTagUseCase createUserTagUseCase;
  @Mock private DeleteUserTagUseCase deleteUserTagUseCase;
  @Mock private UpdateUserTagUseCase updateUserTagUseCase;
  @Mock private GetUserTagsUseCase getUserTagsUseCase;

  @InjectMocks private UserTagController userTagController;

  @Test
  @DisplayName("createTag: Should return 201 Created and map request to command correctly")
  void createTag_ReturnsCreated_WhenRequestIsValid() {
    // Arrange
    UUID userId = UUID.randomUUID();
    CreateUserTagRequest request = new CreateUserTagRequest("Personal", "#FFFFFF");
    CreateUserTagResult expectedResult =
        new CreateUserTagResult(UUID.randomUUID(), "personal", "#FFFFFF");

    when(createUserTagUseCase.execute(any(CreateUserTagCommand.class))).thenReturn(expectedResult);

    // Act
    ResponseEntity<CreateUserTagResult> response = userTagController.createTag(userId, request);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isEqualTo(expectedResult);

    ArgumentCaptor<CreateUserTagCommand> commandCaptor =
        ArgumentCaptor.forClass(CreateUserTagCommand.class);
    verify(createUserTagUseCase).execute(commandCaptor.capture());

    CreateUserTagCommand captured = commandCaptor.getValue();
    assertThat(captured.ownerUserId()).isEqualTo(userId);
    assertThat(captured.name()).isEqualTo("Personal");
    assertThat(captured.colorHex()).isEqualTo("#FFFFFF");
  }

  @Test
  @DisplayName("getTags: Should return 200 OK and the list of tags")
  void getTags_ReturnsOkAndList() {
    // Arrange
    UUID userId = UUID.randomUUID();
    List<UserTagResult> expectedList =
        List.of(
            new UserTagResult(UUID.randomUUID(), "tag1", "#000"),
            new UserTagResult(UUID.randomUUID(), "tag2", "#FFF"));

    when(getUserTagsUseCase.execute(userId)).thenReturn(expectedList);

    // Act
    ResponseEntity<List<UserTagResult>> response = userTagController.getTags(userId);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(2);
    assertThat(response.getBody()).isEqualTo(expectedList);
    verify(getUserTagsUseCase).execute(userId);
  }

  @Test
  @DisplayName("updateTag: Should return 200 OK and map path/body to command correctly")
  void updateTag_ReturnsOk_WhenRequestIsValid() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID tagId = UUID.randomUUID();
    UpdateUserTagRequest request = new UpdateUserTagRequest("Trabajo", "#000000");
    UpdateUserTagResult expectedResult = new UpdateUserTagResult(tagId, "trabajo", "#000000");

    when(updateUserTagUseCase.execute(any(UpdateUserTagCommand.class))).thenReturn(expectedResult);

    // Act
    ResponseEntity<UpdateUserTagResult> response =
        userTagController.updateTag(userId, tagId, request);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(expectedResult);

    ArgumentCaptor<UpdateUserTagCommand> commandCaptor =
        ArgumentCaptor.forClass(UpdateUserTagCommand.class);
    verify(updateUserTagUseCase).execute(commandCaptor.capture());

    UpdateUserTagCommand captured = commandCaptor.getValue();
    assertThat(captured.authenticatedUserId()).isEqualTo(userId);
    assertThat(captured.tagId()).isEqualTo(tagId);
    assertThat(captured.name()).isEqualTo("Trabajo");
  }

  @Test
  @DisplayName("deleteTag: Should return 204 No Content")
  void deleteTag_ReturnsNoContent() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID tagId = UUID.randomUUID();

    // Act
    ResponseEntity<Void> response = userTagController.deleteTag(userId, tagId);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    verify(deleteUserTagUseCase).execute(userId, tagId);
  }
}
