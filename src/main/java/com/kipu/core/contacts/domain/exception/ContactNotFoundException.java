package com.kipu.core.contacts.domain.exception;

import com.kipu.core.common.domain.exception.BusinessException;
import java.util.UUID;
import org.springframework.http.HttpStatus;

public class ContactNotFoundException extends BusinessException {

  public ContactNotFoundException(UUID contactId) {
    super("Contact not found with id: " + contactId, HttpStatus.NOT_FOUND);
  }
}
