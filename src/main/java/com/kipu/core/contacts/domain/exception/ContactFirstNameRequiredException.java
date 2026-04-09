package com.kipu.core.contacts.domain.exception;

import com.kipu.core.common.domain.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class ContactFirstNameRequiredException extends BusinessException {

  public ContactFirstNameRequiredException() {
    super("Contact first name is required and cannot be empty", HttpStatus.BAD_REQUEST);
  }
}
