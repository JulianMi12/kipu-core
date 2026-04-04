package com.kipu.core.identity.domain.exception;

import com.kipu.core.common.domain.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidTokenException extends BusinessException {

  public InvalidTokenException(String message) {
    super(message, HttpStatus.UNAUTHORIZED);
  }
}
