package com.kipu.core.identity.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class BCryptPasswordEncoderAdapterTest {

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private BCryptPasswordEncoderAdapter adapter;

  @Test
  void encode_ShouldDelegateToPasswordEncoder() {
    // Arrange
    String rawPassword = "Password123!";
    String encodedPassword = "encoded_password_hash";
    when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

    // Act
    String result = adapter.encode(rawPassword);

    // Assert
    assertThat(result).isEqualTo(encodedPassword);
    verify(passwordEncoder).encode(rawPassword);
  }

  @Test
  void matches_ShouldReturnTrue_WhenPasswordsMatch() {
    // Arrange
    String rawPassword = "Password123!";
    String encodedPassword = "encoded_password_hash";
    when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

    // Act
    boolean result = adapter.matches(rawPassword, encodedPassword);

    // Assert
    assertThat(result).isTrue();
    verify(passwordEncoder).matches(rawPassword, encodedPassword);
  }

  @Test
  void matches_ShouldReturnFalse_WhenPasswordsDoNotMatch() {
    // Arrange
    String rawPassword = "WrongPassword!";
    String encodedPassword = "encoded_password_hash";
    when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

    // Act
    boolean result = adapter.matches(rawPassword, encodedPassword);

    // Assert
    assertThat(result).isFalse();
    verify(passwordEncoder).matches(rawPassword, encodedPassword);
  }
}
