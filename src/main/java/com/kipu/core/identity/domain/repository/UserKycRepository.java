package com.kipu.core.identity.domain.repository;

import com.kipu.core.identity.domain.model.UserKyc;

public interface UserKycRepository {

  void save(UserKyc userKyc);
}
