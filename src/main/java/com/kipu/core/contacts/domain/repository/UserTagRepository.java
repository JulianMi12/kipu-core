package com.kipu.core.contacts.domain.repository;

import com.kipu.core.contacts.domain.model.UserTag;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserTagRepository {

  UserTag save(UserTag tag);

  Optional<UserTag> findById(UUID id);

  List<UserTag> findByOwnerUserId(UUID ownerUserId);

  void delete(UUID id);

  boolean existsByNameAndOwnerUserId(String name, UUID ownerUserId);

  Optional<UserTag> findByOwnerUserIdAndNameIgnoreCase(UUID ownerUserId, String name);
}
