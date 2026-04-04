package com.kipu.core.identity.infrastructure.persistence.jpa.adapter;

import com.kipu.core.identity.domain.model.User;
import com.kipu.core.identity.domain.repository.UserRepository;
import com.kipu.core.identity.infrastructure.persistence.jpa.entity.UserJpaEntity;
import com.kipu.core.identity.infrastructure.persistence.jpa.repository.JpaUserRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaUserRepositoryAdapter implements UserRepository {

  private final JpaUserRepository jpaUserRepository;

  @Override
  public void save(User user) {
    jpaUserRepository.save(UserJpaEntity.fromDomain(user));
  }

  @Override
  public Optional<User> findById(UUID id) {
    return jpaUserRepository.findById(id).map(UserJpaEntity::toDomain);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return jpaUserRepository.findByEmail(email).map(UserJpaEntity::toDomain);
  }
}
