package com.kipu.core.identity.domain.repository;

import com.kipu.core.identity.domain.model.User;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

  void save(User user);

  Optional<User> findById(UUID id);

  Optional<User> findByEmail(String email);
}
