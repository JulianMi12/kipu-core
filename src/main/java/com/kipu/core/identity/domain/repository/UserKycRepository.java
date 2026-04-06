package com.kipu.core.identity.domain.repository;

import com.kipu.core.identity.domain.model.UserKyc;
import java.util.Optional;
import java.util.UUID;

public interface UserKycRepository {

  void save(UserKyc userKyc);

  Optional<UserKyc> findByUserId(UUID userId);
}
