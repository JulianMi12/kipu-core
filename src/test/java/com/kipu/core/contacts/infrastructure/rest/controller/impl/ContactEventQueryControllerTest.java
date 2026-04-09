package com.kipu.core.contacts.infrastructure.rest.controller.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.contacts.application.event.query.GetUpcomingEventsUseCase;
import com.kipu.core.contacts.application.event.query.UpcomingEventResult;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ContactEventQueryControllerTest {

  @Mock private GetUpcomingEventsUseCase getUpcomingEventsUseCase;

  @InjectMocks private ContactEventQueryController contactEventQueryController;

  @Test
  @DisplayName("getUpcomingEvents: Should return 200 OK and the list of upcoming events")
  void getUpcomingEvents_ReturnsOkAndList_WhenCalled() {
    // Arrange
    UUID userId = UUID.randomUUID();

    UpcomingEventResult event1 =
        new UpcomingEventResult(
            UUID.randomUUID(),
            "Pasta",
            OffsetDateTime.now().plusDays(1),
            List.of(new UpcomingEventResult.TagInfo("amor", "#00FFF8")));

    UpcomingEventResult event2 =
        new UpcomingEventResult(
            UUID.randomUUID(),
            "Pasaporte",
            OffsetDateTime.now().plusYears(4),
            List.of(new UpcomingEventResult.TagInfo("personal", "#4F46E5")));

    List<UpcomingEventResult> expectedResults = List.of(event1, event2);

    when(getUpcomingEventsUseCase.execute(userId)).thenReturn(expectedResults);

    // Act
    ResponseEntity<List<UpcomingEventResult>> response =
        contactEventQueryController.getUpcomingEvents(userId);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).hasSize(2);
    assertThat(response.getBody()).isEqualTo(expectedResults);

    // Verificamos que el título del primer evento sea el esperado (Pasta)
    assertThat(response.getBody().get(0).title()).isEqualTo("Pasta");

    verify(getUpcomingEventsUseCase).execute(userId);
  }

  @Test
  @DisplayName("getUpcomingEvents: Should return 200 OK and empty list when no events found")
  void getUpcomingEvents_ReturnsOkAndEmptyList_WhenNoEvents() {
    // Arrange
    UUID userId = UUID.randomUUID();
    when(getUpcomingEventsUseCase.execute(userId)).thenReturn(List.of());

    // Act
    ResponseEntity<List<UpcomingEventResult>> response =
        contactEventQueryController.getUpcomingEvents(userId);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull().isEmpty();

    verify(getUpcomingEventsUseCase).execute(userId);
  }
}
