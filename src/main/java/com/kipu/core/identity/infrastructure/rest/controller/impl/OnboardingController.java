package com.kipu.core.identity.infrastructure.rest.controller.impl;

import com.kipu.core.identity.application.user.onboarding.CompleteOnboardingCommand;
import com.kipu.core.identity.application.user.onboarding.CompleteOnboardingUseCase;
import com.kipu.core.identity.application.user.profile.UserProfileResult;
import com.kipu.core.identity.infrastructure.rest.controller.OnboardingApi;
import com.kipu.core.identity.infrastructure.rest.dto.OnboardingRequest;
import com.kipu.core.identity.infrastructure.rest.dto.UserProfileResponse;
import com.kipu.core.identity.infrastructure.rest.mapper.UserRestMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/onboarding")
@RequiredArgsConstructor
public class OnboardingController implements OnboardingApi {

  private final UserRestMapper userRestMapper;
  private final CompleteOnboardingUseCase completeOnboardingUseCase;

  @Override
  @PatchMapping("/complete")
  public ResponseEntity<UserProfileResponse> completeOnboarding(
      @AuthenticationPrincipal UUID userId, @RequestBody OnboardingRequest request) {
    log.info("[OnboardingController] PATCH /complete called for user id: {}", userId);
    UserProfileResult result =
        completeOnboardingUseCase.execute(
            new CompleteOnboardingCommand(
                userId,
                request.firstName(),
                request.lastName(),
                request.birthdate(),
                request.timezone()));
    return ResponseEntity.ok(userRestMapper.toUserProfileResponse(result));
  }
}
