package com.kipu.core.identity.domain.exception;

import com.kipu.core.common.domain.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BusinessException {

  public UserNotFoundException() {
    super("User not found.", HttpStatus.NOT_FOUND);
  }
}
