package com.kipu.core.identity.infrastructure.rest.mapper;

import com.kipu.core.identity.application.auth.login.LoginCommand;
import com.kipu.core.identity.application.auth.login.LoginResult;
import com.kipu.core.identity.application.auth.refresh.RefreshCommand;
import com.kipu.core.identity.application.registration.RegisterUserCommand;
import com.kipu.core.identity.application.registration.RegistrationResult;
import com.kipu.core.identity.application.user.profile.UserProfileResult;
import com.kipu.core.identity.domain.model.AuthTokens;
import com.kipu.core.identity.infrastructure.rest.dto.LoginRequest;
import com.kipu.core.identity.infrastructure.rest.dto.LoginResponse;
import com.kipu.core.identity.infrastructure.rest.dto.RefreshRequest;
import com.kipu.core.identity.infrastructure.rest.dto.RefreshResponse;
import com.kipu.core.identity.infrastructure.rest.dto.RegisterUserRequest;
import com.kipu.core.identity.infrastructure.rest.dto.RegistrationResponse;
import com.kipu.core.identity.infrastructure.rest.dto.UserProfileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserRestMapper {

  RegisterUserCommand toCommand(RegisterUserRequest request);

  LoginCommand toCommand(LoginRequest request);

  RefreshCommand toCommand(RefreshRequest request);

  @Mapping(source = "tokens.accessToken", target = "accessToken")
  @Mapping(source = "tokens.refreshToken", target = "refreshToken")
  @Mapping(target = "tokenType", constant = "Bearer")
  RegistrationResponse toRegistrationResponse(RegistrationResult result);

  @Mapping(source = "tokens.accessToken", target = "accessToken")
  @Mapping(source = "tokens.refreshToken", target = "refreshToken")
  @Mapping(target = "tokenType", constant = "Bearer")
  LoginResponse toLoginResponse(LoginResult result);

  @Mapping(target = "tokenType", constant = "Bearer")
  RefreshResponse toRefreshResponse(AuthTokens tokens);

  UserProfileResponse toUserProfileResponse(UserProfileResult result);
}
