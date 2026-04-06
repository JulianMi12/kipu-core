package com.kipu.core.identity.infrastructure.persistence.jpa.adapter;

import com.kipu.core.identity.domain.model.UserKyc;
import com.kipu.core.identity.domain.repository.UserKycRepository;
import com.kipu.core.identity.infrastructure.persistence.jpa.entity.UserKycJpaEntity;
import com.kipu.core.identity.infrastructure.persistence.jpa.repository.JpaUserKycRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaUserKycRepositoryAdapter implements UserKycRepository {

  private final JpaUserKycRepository jpaUserKycRepository;

  @Override
  public void save(UserKyc userKyc) {
    jpaUserKycRepository.save(UserKycJpaEntity.fromDomain(userKyc));
  }

  @Override
  public Optional<UserKyc> findByUserId(UUID userId) {
    return jpaUserKycRepository.findById(userId).map(UserKycJpaEntity::toDomain);
  }
}
