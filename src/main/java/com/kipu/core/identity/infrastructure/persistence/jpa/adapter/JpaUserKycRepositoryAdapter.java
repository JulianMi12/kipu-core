package com.kipu.core.identity.infrastructure.persistence.jpa.adapter;

import com.kipu.core.identity.domain.model.UserKyc;
import com.kipu.core.identity.domain.repository.UserKycRepository;
import com.kipu.core.identity.infrastructure.persistence.jpa.entity.UserKycJpaEntity;
import com.kipu.core.identity.infrastructure.persistence.jpa.repository.JpaUserKycRepository;
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
}
