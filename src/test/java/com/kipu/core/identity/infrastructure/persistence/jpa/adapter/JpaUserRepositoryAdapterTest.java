package com.kipu.core.identity.infrastructure.persistence.jpa.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.identity.domain.model.User;
import com.kipu.core.identity.infrastructure.persistence.jpa.entity.UserJpaEntity;
import com.kipu.core.identity.infrastructure.persistence.jpa.repository.JpaUserRepository;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JpaUserRepositoryAdapterTest {

  @Mock private JpaUserRepository jpaUserRepository;

  @InjectMocks private JpaUserRepositoryAdapter adapter;

  @Test
  void save_ShouldMapToEntityAndSave() {
    // Arrange
    User user =
        User.reconstitute(UUID.randomUUID(), "test@kipu.com", "hash", true, OffsetDateTime.now());
    UserJpaEntity mappedEntity = new UserJpaEntity();

    try (MockedStatic<UserJpaEntity> mockedStatic = mockStatic(UserJpaEntity.class)) {
      mockedStatic.when(() -> UserJpaEntity.fromDomain(user)).thenReturn(mappedEntity);

      // Act
      adapter.save(user);

      // Assert
      verify(jpaUserRepository).save(mappedEntity);
    }
  }

  @Test
  void findById_ShouldReturnMappedUser_WhenEntityExists() {
    // Arrange
    UUID id = UUID.randomUUID();
    String email = "test@kipu.com";
    OffsetDateTime now = OffsetDateTime.now();

    UserJpaEntity entity = new UserJpaEntity();
    entity.setId(id);
    entity.setEmail(email);
    entity.setPasswordHash("hash");
    entity.setActive(true);
    entity.setCreatedAt(now);

    when(jpaUserRepository.findById(id)).thenReturn(Optional.of(entity));

    // Act
    Optional<User> result = adapter.findById(id);

    // Assert
    assertThat(result).isPresent();
    result.ifPresent(
        user -> {
          assertThat(user.getId()).isEqualTo(id);
          assertThat(user.getEmail()).isEqualTo(email);
          assertThat(user.isActive()).isTrue();
        });
    verify(jpaUserRepository).findById(id);
  }

  @Test
  void findById_ShouldReturnEmpty_WhenEntityDoesNotExist() {
    // Arrange
    UUID id = UUID.randomUUID();
    when(jpaUserRepository.findById(id)).thenReturn(Optional.empty());

    // Act
    Optional<User> result = adapter.findById(id);

    // Assert
    assertThat(result).isEmpty();
    verify(jpaUserRepository).findById(id);
  }

  @Test
  void findByEmail_ShouldReturnMappedUser_WhenEntityExists() {
    // Arrange
    String email = "test@kipu.com";
    UUID id = UUID.randomUUID();

    UserJpaEntity entity = new UserJpaEntity();
    entity.setId(id);
    entity.setEmail(email);
    entity.setPasswordHash("hash");
    entity.setActive(true);
    entity.setCreatedAt(OffsetDateTime.now());

    when(jpaUserRepository.findByEmail(email)).thenReturn(Optional.of(entity));

    // Act
    Optional<User> result = adapter.findByEmail(email);

    // Assert
    assertThat(result).isPresent();
    assertThat(result.get().getEmail()).isEqualTo(email);
    verify(jpaUserRepository).findByEmail(email);
  }
}
