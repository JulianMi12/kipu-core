package com.kipu.core.identity.infrastructure.rest.controller.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kipu.core.identity.application.port.out.TokenProviderPort;
import com.kipu.core.identity.application.user.profile.GetUserProfileUseCase;
import com.kipu.core.identity.application.user.profile.UserProfileResult;
import com.kipu.core.identity.domain.model.KycStatus;
import com.kipu.core.identity.infrastructure.rest.dto.UserProfileResponse;
import com.kipu.core.identity.infrastructure.rest.mapper.UserRestMapper;
import com.kipu.core.identity.infrastructure.security.SecurityConfig;
import java.time.OffsetDateTime;
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

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private TokenProviderPort tokenProviderPort;

  @MockitoBean private GetUserProfileUseCase getUserProfileUseCase;
  @MockitoBean private UserRestMapper userRestMapper;

  @Test
  @DisplayName("GET /basic-info: Should return profile when user is authenticated")
  void getMyProfile_ShouldReturnProfile_WhenAuthenticated() throws Exception {
    // Arrange
    OffsetDateTime now = OffsetDateTime.now();
    UUID userId = UUID.randomUUID();
    String email = "dev@kipu.com";

    UserProfileResult result =
        new UserProfileResult(
            userId, email, true, now, KycStatus.PENDING, false, "Julian", "Miranda", null);
    UserProfileResponse response =
        UserProfileResponse.builder().id(userId).email(email).active(true).createdAt(now).build();

    when(getUserProfileUseCase.execute(userId)).thenReturn(result);
    when(userRestMapper.toUserProfileResponse(result)).thenReturn(response);

    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(userId, null, java.util.List.of());

    // Act & Assert
    mockMvc
        .perform(
            get("/api/v1/users/basic-info")
                .with(SecurityMockMvcRequestPostProcessors.authentication(auth))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.email").value(email))
        .andExpect(jsonPath("$.active").value(true));

    verify(getUserProfileUseCase).execute(userId);
  }

  @Test
  @DisplayName("GET /basic-info: Should return 401 when no authentication is provided")
  void getMyProfile_ShouldReturnUnauthorized_WhenNotAuthenticated() throws Exception {
    // Act & Assert
    mockMvc
        .perform(get("/api/v1/users/basic-info").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }
}
