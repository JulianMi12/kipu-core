package com.kipu.core.identity.infrastructure.contacts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.identity.domain.model.UserKyc;
import com.kipu.core.identity.domain.repository.UserKycRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SelfContactLookupAdapterTest {

  @Mock private UserKycRepository userKycRepository;

  @InjectMocks private SelfContactLookupAdapter selfContactLookupAdapter;

  @Test
  @DisplayName("findSelfContactId: Should return selfContactId when user KYC exists")
  void findSelfContactId_ShouldReturnId_WhenKycExists() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID expectedContactId = UUID.randomUUID();

    // Mockeamos el objeto UserKyc
    UserKyc kyc = mock(UserKyc.class);
    when(kyc.getSelfContactId()).thenReturn(expectedContactId);

    when(userKycRepository.findByUserId(userId)).thenReturn(Optional.of(kyc));

    // Act
    Optional<UUID> result = selfContactLookupAdapter.findSelfContactId(userId);

    // Assert
    assertThat(result).isPresent().contains(expectedContactId);
    verify(userKycRepository).findByUserId(userId);
  }

  @Test
  @DisplayName("findSelfContactId: Should return empty Optional when user KYC does not exist")
  void findSelfContactId_ShouldReturnEmpty_WhenKycDoesNotExist() {
    // Arrange
    UUID userId = UUID.randomUUID();
    when(userKycRepository.findByUserId(userId)).thenReturn(Optional.empty());

    // Act
    Optional<UUID> result = selfContactLookupAdapter.findSelfContactId(userId);

    // Assert
    assertThat(result).isEmpty();
    verify(userKycRepository).findByUserId(userId);
  }
}
