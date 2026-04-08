package com.kipu.core.contacts.domain.exception;

import com.kipu.core.common.domain.exception.BusinessException;
import java.util.UUID;
import org.springframework.http.HttpStatus;

public class UserTagNotFoundException extends BusinessException {

  public UserTagNotFoundException(UUID tagId) {
    super("UserTag not found with id: " + tagId, HttpStatus.NOT_FOUND);
  }
}
