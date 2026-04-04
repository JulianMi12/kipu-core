package com.kipu.core.identity.domain.exception;

import com.kipu.core.common.domain.exception.BusinessException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserAlreadyExistsException extends BusinessException {

  private final String email;

  public UserAlreadyExistsException(String email) {
    super(String.format("User with email '%s' already exists", email), HttpStatus.CONFLICT);
    this.email = email;
  }
}
