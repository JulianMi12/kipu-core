package com.kipu.core.identity.domain.exception;

import com.kipu.core.common.domain.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class ProfileSyncException extends BusinessException {

  public ProfileSyncException(String detail) {
    super("Failed to sync profile data: " + detail, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  public ProfileSyncException(String detail, Throwable cause) {
    super("Failed to sync profile data: " + detail, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
