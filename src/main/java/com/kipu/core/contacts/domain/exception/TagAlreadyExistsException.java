package com.kipu.core.contacts.domain.exception;

import com.kipu.core.common.domain.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class TagAlreadyExistsException extends BusinessException {

  public TagAlreadyExistsException(String tagName) {
    super(
        String.format("Tag with name '%s' already exists for this user.", tagName),
        HttpStatus.CONFLICT);
  }
}
