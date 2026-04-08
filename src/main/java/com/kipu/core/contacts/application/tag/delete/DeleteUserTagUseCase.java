package com.kipu.core.contacts.application.tag.delete;

import com.kipu.core.contacts.domain.exception.UnauthorizedContactAccessException;
import com.kipu.core.contacts.domain.exception.UserTagNotFoundException;
import com.kipu.core.contacts.domain.model.UserTag;
import com.kipu.core.contacts.domain.repository.UserTagRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DeleteUserTagUseCase {

  private final UserTagRepository userTagRepository;

  public void execute(UUID authenticatedUserId, UUID tagId) {
    log.info("[DeleteUserTagUseCase] Starting process with id: {}", tagId);

    UserTag tag =
        userTagRepository.findById(tagId).orElseThrow(() -> new UserTagNotFoundException(tagId));

    if (!tag.getOwnerUserId().equals(authenticatedUserId)) {
      log.error(
          "[DeleteUserTagUseCase] Error occurred during authorization: user {} does not own tag {}",
          authenticatedUserId,
          tagId);
      throw new UnauthorizedContactAccessException();
    }

    userTagRepository.delete(tagId);

    log.info("[DeleteUserTagUseCase] Process completed successfully for id: {}", tagId);
  }
}
