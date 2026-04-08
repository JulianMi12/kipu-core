package com.kipu.core.contacts.application.tag.create;

import com.kipu.core.contacts.domain.exception.TagAlreadyExistsException;
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
public class CreateUserTagUseCase {

  private final UserTagRepository userTagRepository;

  public CreateUserTagResult execute(CreateUserTagCommand command) {
    log.info(
        "[CreateUserTagUseCase] Starting process for owner user id: {}", command.ownerUserId());

    String normalizedName = command.name().trim().toLowerCase();

    if (userTagRepository.existsByNameAndOwnerUserId(normalizedName, command.ownerUserId())) {
      log.warn(
          "[CreateUserTagUseCase] Conflict: Tag '{}' already exists for user {}",
          normalizedName,
          command.ownerUserId());
      throw new TagAlreadyExistsException(normalizedName);
    }

    UserTag tag = UserTag.create(command.ownerUserId(), command.name(), command.colorHex());
    UserTag saved = userTagRepository.save(tag);

    log.info("[CreateUserTagUseCase] Process completed successfully for id: {}", saved.getId());
    return CreateUserTagResult.from(saved);
  }
}
