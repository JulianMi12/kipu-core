package com.kipu.core.identity.application.user.profile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.identity.domain.exception.UserNotFoundException;
import com.kipu.core.identity.domain.model.User;
import com.kipu.core.identity.domain.repository.UserKycRepository;
import com.kipu.core.identity.domain.repository.UserRepository;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetUserProfileUseCaseTest {

  @Mock private UserRepository userRepository;
  @Mock private UserKycRepository userKycRepository;

  @InjectMocks private GetUserProfileUseCase getUserProfileUseCase;

  @Test
  void execute_ShouldReturnProfile_WhenUserExists() {
    // Arrange
    UUID userId = UUID.randomUUID();
    OffsetDateTime now = OffsetDateTime.now();

    User mockUser = User.reconstitute(userId, "test@kipu.com", "hash123", true, now);

    when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

    // Act
    UserProfileResult result = getUserProfileUseCase.execute(userId);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(userId);
    assertThat(result.email()).isEqualTo("test@kipu.com");
    assertThat(result.active()).isTrue();
    assertThat(result.createdAt()).isEqualTo(now);
    verify(userRepository).findById(userId);
  }

  @Test
  void execute_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
    // Arrange
    UUID userId = UUID.randomUUID();
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> getUserProfileUseCase.execute(userId))
        .isInstanceOf(UserNotFoundException.class);

    verify(userRepository).findById(userId);
  }
}
