package com.kipu.core.contacts.domain.exception;

import com.kipu.core.common.domain.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class CannotDeleteSelfContactException extends BusinessException {

  public CannotDeleteSelfContactException() {
    super("Cannot delete the self-contact of a user", HttpStatus.CONFLICT);
  }
}
