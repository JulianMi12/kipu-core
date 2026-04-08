package com.kipu.core.contacts.application.tag.get;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.domain.model.UserTag;
import com.kipu.core.contacts.domain.repository.UserTagRepository;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetUserTagsUseCaseTest {

  @Mock private UserTagRepository userTagRepository;

  @InjectMocks private GetUserTagsUseCase getUserTagsUseCase;

  @Test
  @DisplayName("execute: Should return list of tags when user has tags")
  void execute_ShouldReturnTagsList_WhenUserHasTags() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UserTag mockTag1 = mock(UserTag.class);
    UserTag mockTag2 = mock(UserTag.class);

    // Configuramos comportamientos básicos para el mapeo a UserTagResult
    when(mockTag1.getId()).thenReturn(UUID.randomUUID());
    when(mockTag1.getName()).thenReturn("personal");
    when(mockTag1.getColorHex()).thenReturn("#FFFFFF");

    when(mockTag2.getId()).thenReturn(UUID.randomUUID());
    when(mockTag2.getName()).thenReturn("trabajo");
    when(mockTag2.getColorHex()).thenReturn("#000000");

    when(userTagRepository.findByOwnerUserId(userId)).thenReturn(List.of(mockTag1, mockTag2));

    // Act
    List<UserTagResult> result = getUserTagsUseCase.execute(userId);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("personal", result.get(0).name());
    assertEquals("trabajo", result.get(1).name());

    verify(userTagRepository).findByOwnerUserId(userId);
  }

  @Test
  @DisplayName("execute: Should return empty list when user has no tags")
  void execute_ShouldReturnEmptyList_WhenUserHasNoTags() {
    // Arrange
    UUID userId = UUID.randomUUID();
    when(userTagRepository.findByOwnerUserId(userId)).thenReturn(Collections.emptyList());

    // Act
    List<UserTagResult> result = getUserTagsUseCase.execute(userId);

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(userTagRepository).findByOwnerUserId(userId);
  }
}
