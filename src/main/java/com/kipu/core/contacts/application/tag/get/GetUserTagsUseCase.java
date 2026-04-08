package com.kipu.core.contacts.application.tag.get;

import com.kipu.core.contacts.domain.repository.UserTagRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetUserTagsUseCase {

  private final UserTagRepository userTagRepository;

  public List<UserTagResult> execute(UUID ownerUserId) {
    log.info("[GetUserTagsUseCase] Starting process for user id: {}", ownerUserId);

    List<UserTagResult> results =
        userTagRepository.findByOwnerUserId(ownerUserId).stream().map(UserTagResult::from).toList();

    log.info("[GetUserTagsUseCase] Process completed successfully for user id: {}", ownerUserId);
    return results;
  }
}
