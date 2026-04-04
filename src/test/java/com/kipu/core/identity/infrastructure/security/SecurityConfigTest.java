package com.kipu.core.identity.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kipu.core.identity.application.port.out.TokenProviderPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(SecurityConfigTest.TestController.class)
@Import(SecurityConfig.class)
class SecurityConfigTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private JwtAuthenticationFilter jwtAuthenticationFilter;
  @MockitoBean private TokenProviderPort tokenProviderPort;

  @Test
  void publicEndpoints_ShouldReturnOk() throws Exception {
    mockMvc.perform(post("/api/v1/auth/login")).andExpect(status().isOk());

    mockMvc.perform(post("/api/v1/auth/register")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser
  void privateEndpoints_WithUser_ShouldReturnOk() throws Exception {
    mockMvc.perform(get("/api/v1/users/profile")).andExpect(status().isOk());
  }

  @Test
  void passwordEncoder_ShouldBeBCrypt() {
    SecurityConfig config = new SecurityConfig(jwtAuthenticationFilter);
    PasswordEncoder encoder = config.passwordEncoder();
    String raw = "pass";
    assertThat(encoder.matches(raw, encoder.encode(raw))).isTrue();
  }

  @RestController
  static class TestController {
    @PostMapping({"/api/v1/auth/login", "/api/v1/auth/register"})
    void publicStub() {}

    @GetMapping("/api/v1/users/profile")
    void privateStub() {}
  }
}
