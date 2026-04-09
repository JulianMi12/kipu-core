package com.kipu.core.identity.infrastructure.rest.controller.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kipu.core.identity.application.port.out.TokenProviderPort;
import com.kipu.core.identity.application.user.onboarding.CompleteOnboardingCommand;
import com.kipu.core.identity.application.user.onboarding.CompleteOnboardingUseCase;
import com.kipu.core.identity.application.user.profile.UserProfileResult;
import com.kipu.core.identity.domain.model.KycStatus;
import com.kipu.core.identity.infrastructure.rest.dto.OnboardingRequest;
import com.kipu.core.identity.infrastructure.rest.dto.UserProfileResponse;
import com.kipu.core.identity.infrastructure.rest.mapper.UserRestMapper;
import com.kipu.core.identity.infrastructure.security.SecurityConfig;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OnboardingController.class)
@Import(SecurityConfig.class)
class OnboardingControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private TokenProviderPort tokenProviderPort;
  @MockitoBean private CompleteOnboardingUseCase completeOnboardingUseCase;
  @MockitoBean private UserRestMapper userRestMapper;

  @Test
  @DisplayName("PATCH /complete: Should return 200 and profile when onboarding is completed")
  void completeOnboarding_ShouldReturnOk_WhenAuthenticatedAndRequestIsValid() throws Exception {
    // Arrange
    UUID userId = UUID.randomUUID();
    String email = "dev@kipu.com";
    LocalDate birthdate = LocalDate.of(1990, 1, 1);
    OffsetDateTime now = OffsetDateTime.now();

    OnboardingRequest request =
        new OnboardingRequest("Julian", "Miranda", birthdate, "America/Bogota");

    UserProfileResult result =
        new UserProfileResult(
            userId, email, true, now, KycStatus.COMPLETED, true, "Julian", "Miranda", null);

    UserProfileResponse response =
        UserProfileResponse.builder().id(userId).email(email).active(true).createdAt(now).build();

    when(completeOnboardingUseCase.execute(any(CompleteOnboardingCommand.class)))
        .thenReturn(result);
    when(userRestMapper.toUserProfileResponse(result)).thenReturn(response);

    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(userId, null, List.of());

    // Act & Assert
    mockMvc
        .perform(
            patch("/api/v1/onboarding/complete")
                .with(SecurityMockMvcRequestPostProcessors.authentication(auth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.email").value(email));

    verify(completeOnboardingUseCase).execute(any(CompleteOnboardingCommand.class));
  }

  @Test
  @DisplayName("PATCH /complete: Should return 403 when no authentication is provided")
  void completeOnboarding_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
    // Arrange
    OnboardingRequest request =
        new OnboardingRequest("Julian", "Miranda", LocalDate.now(), "America/Bogota");

    // Act & Assert
    mockMvc
        .perform(
            patch("/api/v1/onboarding/complete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());
  }
}
