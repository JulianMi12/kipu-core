package com.kipu.core.identity.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kipu.core.identity.application.port.out.TokenProviderPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

  @Mock private TokenProviderPort tokenProviderPort;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private FilterChain filterChain;

  @InjectMocks private JwtAuthenticationFilter jwtAuthenticationFilter;

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void doFilterInternal_ShouldContinueChain_WhenNoAuthHeader()
      throws ServletException, IOException {
    // Arrange
    when(request.getHeader("Authorization")).thenReturn(null);

    // Act
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Assert
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternal_ShouldContinueChain_WhenHeaderNotBearer()
      throws ServletException, IOException {
    // Arrange
    when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

    // Act
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Assert
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternal_ShouldContinueChain_WhenTokenIsInvalid()
      throws ServletException, IOException {
    // Arrange
    String token = "invalid-token";
    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
    when(tokenProviderPort.isAccessTokenValid(token)).thenReturn(false);

    // Act
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Assert
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternal_ShouldSetAuthentication_WhenTokenIsValid()
      throws ServletException, IOException {
    // Arrange
    String token = "valid-token";
    UUID userId = UUID.randomUUID();
    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
    when(tokenProviderPort.isAccessTokenValid(token)).thenReturn(true);
    when(tokenProviderPort.extractUserId(token)).thenReturn(userId);

    // Act
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Assert
    var auth = SecurityContextHolder.getContext().getAuthentication();
    assertThat(auth).isNotNull();
    assertThat(auth.getPrincipal()).isEqualTo(userId);
    verify(filterChain).doFilter(request, response);
  }
}
