package com.kipu.core.contacts.application.contact.get;

import com.kipu.core.contacts.domain.repository.ContactRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetUserContactsUseCase {

  private final ContactRepository contactRepository;

  public Page<ContactSummaryResult> execute(
      UUID ownerUserId, UUID selfContactId, Pageable pageable) {
    log.info("[GetUserContactsUseCase] Starting process for user id: {}", ownerUserId);

    Page<ContactSummaryResult> page =
        contactRepository
            .findAllByOwnerUserId(ownerUserId, selfContactId, pageable)
            .map(ContactSummaryResult::from);

    log.info(
        "[GetUserContactsUseCase] Process completed successfully for user id: {}", ownerUserId);
    return page;
  }
}
