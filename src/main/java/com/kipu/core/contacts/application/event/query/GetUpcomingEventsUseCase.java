package com.kipu.core.contacts.application.event.query;

import com.kipu.core.contacts.domain.model.ContactEvent;
import com.kipu.core.contacts.domain.model.UserTag;
import com.kipu.core.contacts.domain.repository.ContactEventRepository;
import com.kipu.core.contacts.domain.repository.UserTagRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetUpcomingEventsUseCase {

  private static final int UPCOMING_LIMIT = 4;
  private final ContactEventRepository contactEventRepository;
  private final UserTagRepository userTagRepository;

  @Transactional(readOnly = true)
  public List<UpcomingEventResult> execute(UUID ownerUserId) {
    log.info("[GetUpcomingEventsUseCase] Fetching upcoming events for owner: {}", ownerUserId);

    List<ContactEvent> events =
        contactEventRepository.findUpcomingByOwnerUserId(
            ownerUserId, OffsetDateTime.now(), UPCOMING_LIMIT);

    if (events.isEmpty()) return List.of();

    Set<UUID> requiredTagIds =
        events.stream().flatMap(event -> event.getTagIds().stream()).collect(Collectors.toSet());

    Map<UUID, UserTag> tagMap =
        userTagRepository.findAllById(requiredTagIds).stream()
            .collect(Collectors.toMap(UserTag::getId, Function.identity()));

    return events.stream().map(event -> UpcomingEventResult.from(event, tagMap)).toList();
  }
}
