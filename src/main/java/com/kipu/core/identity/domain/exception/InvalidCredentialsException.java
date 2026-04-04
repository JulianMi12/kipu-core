package com.kipu.core.identity.domain.exception;

import com.kipu.core.common.domain.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends BusinessException {

  public InvalidCredentialsException() {
    super("Invalid email or password", HttpStatus.UNAUTHORIZED);
  }
}
