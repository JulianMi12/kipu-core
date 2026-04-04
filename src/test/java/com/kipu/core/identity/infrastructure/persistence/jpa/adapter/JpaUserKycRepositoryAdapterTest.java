package com.kipu.core.identity.infrastructure.persistence.jpa.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.kipu.core.identity.domain.model.KycStatus;
import com.kipu.core.identity.domain.model.UserKyc;
import com.kipu.core.identity.infrastructure.persistence.jpa.entity.UserKycJpaEntity;
import com.kipu.core.identity.infrastructure.persistence.jpa.repository.JpaUserKycRepository;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
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
  void save_ShouldMapToEntityAndSave() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID selfContactId = UUID.randomUUID();
    LocalDate birthdate = LocalDate.of(1995, 8, 20);

    UserKyc domainKyc = UserKyc.reconstitute(
        userId,
        selfContactId,
        KycStatus.IN_PROGRESS,
        false,
        birthdate,
        OffsetDateTime.now()
    );

    ArgumentCaptor<UserKycJpaEntity> entityCaptor =
        ArgumentCaptor.forClass(UserKycJpaEntity.class);

    // Act
    adapter.save(domainKyc);

    // Assert
    verify(jpaUserKycRepository).save(entityCaptor.capture());
    UserKycJpaEntity savedEntity = entityCaptor.getValue();

    assertThat(savedEntity.getUserId()).isEqualTo(userId);
    assertThat(savedEntity.getSelfContactId()).isEqualTo(selfContactId);
    assertThat(savedEntity.getStatus()).isEqualTo(KycStatus.IN_PROGRESS);
    assertThat(savedEntity.getBirthdate()).isEqualTo(birthdate);
    assertThat(savedEntity.isOnboardingCompleted()).isFalse();
    assertThat(savedEntity.getUpdatedAt()).isNotNull();
  }
}