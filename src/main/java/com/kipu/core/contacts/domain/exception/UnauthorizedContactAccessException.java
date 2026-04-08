package com.kipu.core.contacts.domain.exception;

import com.kipu.core.common.domain.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class UnauthorizedContactAccessException extends BusinessException {

  public UnauthorizedContactAccessException() {
    super("Access denied: you do not own this contact.", HttpStatus.FORBIDDEN);
  }
}
