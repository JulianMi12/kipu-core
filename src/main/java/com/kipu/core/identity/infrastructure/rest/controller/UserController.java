package com.kipu.core.identity.infrastructure.rest.controller;

import com.kipu.core.identity.application.user.profile.GetUserProfileUseCase;
import com.kipu.core.identity.application.user.profile.UserProfileResult;
import com.kipu.core.identity.infrastructure.rest.dto.UserProfileResponse;
import com.kipu.core.identity.infrastructure.rest.mapper.UserRestMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController implements UserApi {

  private final GetUserProfileUseCase getUserProfileUseCase;
  private final UserRestMapper userRestMapper;

  @Override
  @GetMapping("/basic-info")
  public ResponseEntity<UserProfileResponse> getMyProfile(@AuthenticationPrincipal UUID userId) {
    log.info("[UserController] GET /basic-info called for user id: {}", userId);
    UserProfileResult result = getUserProfileUseCase.execute(userId);
    return ResponseEntity.ok(userRestMapper.toUserProfileResponse(result));
  }
}
