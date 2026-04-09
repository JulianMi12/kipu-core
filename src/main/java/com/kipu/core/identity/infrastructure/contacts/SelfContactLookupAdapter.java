package com.kipu.core.identity.infrastructure.contacts;

import com.kipu.core.contacts.domain.port.out.SelfContactLookupPort;
import com.kipu.core.identity.domain.repository.UserKycRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SelfContactLookupAdapter implements SelfContactLookupPort {

  private final UserKycRepository userKycRepository;

  @Override
  public Optional<UUID> findSelfContactId(UUID userId) {
    log.debug("[SelfContactLookupAdapter] Looking up self-contact id for user id: {}", userId);
    return userKycRepository.findByUserId(userId).map(kyc -> kyc.getSelfContactId());
  }
}
