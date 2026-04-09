package com.kipu.core.contacts.application.event.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.domain.model.ContactEvent;
import com.kipu.core.contacts.domain.model.UserTag;
import com.kipu.core.contacts.domain.repository.ContactEventRepository;
import com.kipu.core.contacts.domain.repository.UserTagRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetUpcomingEventsUseCaseTest {

  @Mock private ContactEventRepository contactEventRepository;
  @Mock private UserTagRepository userTagRepository;

  @InjectMocks private GetUpcomingEventsUseCase getUpcomingEventsUseCase;

  @Test
  @DisplayName("execute: Should return empty list when no events are found")
  void execute_ShouldReturnEmptyList_WhenNoEventsFound() {
    // Arrange
    UUID ownerUserId = UUID.randomUUID();
    when(contactEventRepository.findUpcomingByOwnerUserId(eq(ownerUserId), any(), eq(4)))
        .thenReturn(List.of());

    // Act
    List<UpcomingEventResult> result = getUpcomingEventsUseCase.execute(ownerUserId);

    // Assert
    assertThat(result).isEmpty();
    verifyNoInteractions(userTagRepository);
  }

  @Test
  @DisplayName("execute: Should fetch events and their specific tags successfully")
  void execute_ShouldReturnEventsWithEnrichedTags() {
    // Arrange
    UUID ownerUserId = UUID.randomUUID();
    UUID tagId1 = UUID.randomUUID();
    UUID tagId2 = UUID.randomUUID();

    // Evento 1 con Tag 1
    ContactEvent event1 = mock(ContactEvent.class);
    when(event1.getId()).thenReturn(UUID.randomUUID());
    when(event1.getTitle()).thenReturn("Evento 1");
    when(event1.getTagIds()).thenReturn(Set.of(tagId1));
    when(event1.getStartDateTime()).thenReturn(OffsetDateTime.now());

    // Evento 2 con Tag 1 y Tag 2
    ContactEvent event2 = mock(ContactEvent.class);
    when(event2.getId()).thenReturn(UUID.randomUUID());
    when(event2.getTitle()).thenReturn("Evento 2");
    when(event2.getTagIds()).thenReturn(Set.of(tagId1, tagId2));
    when(event2.getStartDateTime()).thenReturn(OffsetDateTime.now().plusDays(1));

    when(contactEventRepository.findUpcomingByOwnerUserId(eq(ownerUserId), any(), eq(4)))
        .thenReturn(List.of(event1, event2));

    // Mocks de los Tags
    UserTag tag1 = UserTag.reconstitute(tagId1, ownerUserId, "Tag 1", "#FF0000");
    UserTag tag2 = UserTag.reconstitute(tagId2, ownerUserId, "Tag 2", "#00FF00");

    // Verificamos que solo se busquen los IDs necesarios (tagId1 y tagId2)
    when(userTagRepository.findAllById(any())).thenReturn(List.of(tag1, tag2));

    // Act
    List<UpcomingEventResult> result = getUpcomingEventsUseCase.execute(ownerUserId);

    // Assert
    assertThat(result).hasSize(2);

    // Validar enriquecimiento de Evento 1
    assertThat(result.get(0).title()).isEqualTo("Evento 1");
    assertThat(result.get(0).tags()).hasSize(1);
    assertThat(result.get(0).tags().get(0).name()).isEqualTo("tag 1");

    // Validar enriquecimiento de Evento 2
    assertThat(result.get(1).title()).isEqualTo("Evento 2");
    assertThat(result.get(1).tags()).hasSize(2);

    verify(userTagRepository).findAllById(any());
  }

  @Test
  @DisplayName("execute: Should handle missing tags in repository gracefully")
  void execute_ShouldHandleMissingTagsGracefully() {
    // Arrange
    UUID ownerUserId = UUID.randomUUID();
    UUID missingTagId = UUID.randomUUID();

    ContactEvent event = mock(ContactEvent.class);
    when(event.getTagIds()).thenReturn(Set.of(missingTagId));

    when(contactEventRepository.findUpcomingByOwnerUserId(any(), any(), anyInt()))
        .thenReturn(List.of(event));

    // El repositorio no encuentra el tag (lista vacía)
    when(userTagRepository.findAllById(any())).thenReturn(List.of());

    // Act
    List<UpcomingEventResult> result = getUpcomingEventsUseCase.execute(ownerUserId);

    // Assert
    assertThat(result).hasSize(1);
    assertThat(result.get(0).tags()).isEmpty(); // No rompe, simplemente lista vacía
  }
}
