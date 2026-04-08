package com.kipu.core.contacts.domain.exception;

import com.kipu.core.common.domain.exception.BusinessException;
import java.util.UUID;
import org.springframework.http.HttpStatus;

public class ContactEventNotFoundException extends BusinessException {

  public ContactEventNotFoundException(UUID eventId) {
    super("Contact event not found with id: " + eventId, HttpStatus.NOT_FOUND);
  }
}
