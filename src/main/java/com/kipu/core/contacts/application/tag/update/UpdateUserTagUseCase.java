package com.kipu.core.contacts.application.tag.update;

import com.kipu.core.contacts.domain.exception.TagAlreadyExistsException;
import com.kipu.core.contacts.domain.exception.UnauthorizedContactAccessException;
import com.kipu.core.contacts.domain.exception.UserTagNotFoundException;
import com.kipu.core.contacts.domain.model.UserTag;
import com.kipu.core.contacts.domain.repository.UserTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UpdateUserTagUseCase {

  private final UserTagRepository userTagRepository;

  public UpdateUserTagResult execute(UpdateUserTagCommand command) {
    log.info("[UpdateUserTagUseCase] Starting process with id: {}", command.tagId());

    UserTag tag =
        userTagRepository
            .findById(command.tagId())
            .orElseThrow(() -> new UserTagNotFoundException(command.tagId()));

    if (!tag.getOwnerUserId().equals(command.authenticatedUserId())) {
      log.error(
          "[UpdateUserTagUseCase] Error occurred during authorization: user {} does not own tag {}",
          command.authenticatedUserId(),
          command.tagId());
      throw new UnauthorizedContactAccessException();
    }

    String normalizedName = command.name().trim().toLowerCase();

    if (!tag.getName().equals(normalizedName)) {
      if (userTagRepository.existsByNameAndOwnerUserId(normalizedName, tag.getOwnerUserId())) {
        log.warn(
            "[UpdateUserTagUseCase] Conflict: Name '{}' already taken for user {}",
            normalizedName,
            tag.getOwnerUserId());
        throw new TagAlreadyExistsException(normalizedName);
      }
    }

    tag.update(command.name(), command.colorHex());
    UserTag saved = userTagRepository.save(tag);

    log.info("[UpdateUserTagUseCase] Process completed successfully for id: {}", command.tagId());
    return UpdateUserTagResult.from(saved);
  }
}
