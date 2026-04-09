package com.kipu.core.contacts.infrastructure.rest.controller.impl;

import com.kipu.core.contacts.application.event.query.GetUpcomingEventsUseCase;
import com.kipu.core.contacts.application.event.query.UpcomingEventResult;
import com.kipu.core.contacts.infrastructure.rest.controller.ContactEventQueryApi;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/contacts/events")
@RequiredArgsConstructor
public class ContactEventQueryController implements ContactEventQueryApi {

  private final GetUpcomingEventsUseCase getUpcomingEventsUseCase;

  @Override
  @GetMapping("/upcoming")
  public ResponseEntity<List<UpcomingEventResult>> getUpcomingEvents(
      @AuthenticationPrincipal UUID userId) {
    log.info(
        "[ContactEventQueryController] GET /contacts/events/upcoming called for userId: {}",
        userId);
    List<UpcomingEventResult> result = getUpcomingEventsUseCase.execute(userId);
    return ResponseEntity.ok(result);
  }
}
