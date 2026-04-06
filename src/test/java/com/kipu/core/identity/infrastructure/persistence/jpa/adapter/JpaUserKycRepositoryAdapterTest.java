package com.kipu.core.identity.infrastructure.persistence.jpa.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.identity.domain.model.KycStatus;
import com.kipu.core.identity.domain.model.UserKyc;
import com.kipu.core.identity.infrastructure.persistence.jpa.entity.UserKycJpaEntity;
import com.kipu.core.identity.infrastructure.persistence.jpa.repository.JpaUserKycRepository;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JpaUserKycRepositoryAdapterTest {

  @Mock private JpaUserKycRepository jpaUserKycRepository;

  @InjectMocks private JpaUserKycRepositoryAdapter adapter;

  @Test
  @DisplayName("save: Should map domain kyc to entity and save correctly")
  void save_ShouldMapToEntityAndSave() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID selfContactId = UUID.randomUUID();
    OffsetDateTime now = OffsetDateTime.now();

    UserKyc domainKyc =
        UserKyc.reconstitute(userId, selfContactId, KycStatus.IN_PROGRESS, false, now);

    ArgumentCaptor<UserKycJpaEntity> entityCaptor = ArgumentCaptor.forClass(UserKycJpaEntity.class);

    // Act
    adapter.save(domainKyc);

    // Assert
    verify(jpaUserKycRepository).save(entityCaptor.capture());
    UserKycJpaEntity savedEntity = entityCaptor.getValue();

    assertThat(savedEntity.getUserId()).isEqualTo(userId);
    assertThat(savedEntity.getSelfContactId()).isEqualTo(selfContactId);
    assertThat(savedEntity.getStatus()).isEqualTo(KycStatus.IN_PROGRESS);
    assertThat(savedEntity.isOnboardingCompleted()).isFalse();
    assertThat(savedEntity.getUpdatedAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("findByUserId: Should return domain kyc when entity exists in database")
  void findByUserId_ShouldReturnUserKyc_WhenEntityExists() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID selfContactId = UUID.randomUUID();
    OffsetDateTime now = OffsetDateTime.now();

    UserKycJpaEntity jpaEntity =
        new UserKycJpaEntity(userId, selfContactId, KycStatus.COMPLETED, true, now);

    when(jpaUserKycRepository.findById(userId)).thenReturn(Optional.of(jpaEntity));

    // Act
    Optional<UserKyc> result = adapter.findByUserId(userId);

    // Assert
    assertThat(result).isPresent();
    UserKyc domainKyc = result.get();
    assertThat(domainKyc.getUserId()).isEqualTo(userId);
    assertThat(domainKyc.getSelfContactId()).isEqualTo(selfContactId);
    assertThat(domainKyc.getStatus()).isEqualTo(KycStatus.COMPLETED);
    assertThat(domainKyc.isOnboardingCompleted()).isTrue();
    assertThat(domainKyc.getUpdatedAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("findByUserId: Should return empty optional when entity does not exist")
  void findByUserId_ShouldReturnEmpty_WhenEntityDoesNotExist() {
    // Arrange
    UUID userId = UUID.randomUUID();
    when(jpaUserKycRepository.findById(userId)).thenReturn(Optional.empty());

    // Act
    Optional<UserKyc> result = adapter.findByUserId(userId);

    // Assert
    assertThat(result).isEmpty();
    verify(jpaUserKycRepository).findById(userId);
  }
}
