package com.backend.ims.data.user.impl.service;

import com.backend.ims.data.activitylog.api.service.ActivityLogService;
import com.backend.ims.data.user.api.common.UserCommon;
import com.backend.ims.data.user.api.model.User;
import com.backend.ims.data.user.api.model.request.ChangePasswordRequest;
import com.backend.ims.data.user.api.model.request.LoginRequest;
import com.backend.ims.data.user.api.model.request.RegistrationRequest;
import com.backend.ims.data.user.api.model.response.AuthResponse;
import com.backend.ims.data.user.api.service.AuthService;
import com.backend.ims.data.user.impl.accessor.UserAccessor;
import com.backend.ims.general.model.BaseResponse;
import com.backend.ims.general.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

  private final UserAccessor userAccessor;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  @Autowired
  private ActivityLogService activityLogService;

  @Autowired
  public AuthServiceImpl(UserAccessor userAccessor,
                         PasswordEncoder passwordEncoder,
                         JwtUtil jwtUtil) {
    this.userAccessor = userAccessor;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
  }

  @Override
  public ResponseEntity<?> register(RegistrationRequest request) {
    try {
      if (userAccessor.isExist(UserCommon.USERNAME, request.getUsername())) {
        return ResponseEntity
          .badRequest()
          .body(new BaseResponse<>("Error: Username is already taken!"));
      }

      if (userAccessor.isExist(UserCommon.EMAIL, request.getEmail())) {
        return ResponseEntity
          .badRequest()
          .body(new BaseResponse<>("Error: Email is already in use!"));
      }

      User user = User.builder()
        .id(UUID.randomUUID().toString())
        .email(request.getEmail())
        .username(request.getUsername())
        .password(passwordEncoder.encode(request.getPassword()))
        .gender(request.getGender() == null ? "Other" : request.getGender())
        .phoneNumber(request.getPhoneNumber() == null ? "" : request.getPhoneNumber())
        .dob(request.getDob())
        .createdDate(System.currentTimeMillis())
        .enabled(true) // TODO: Need to add capability to confirm via email, make false after we have capability
        .roles(List.of("ROLE_USER"))
        .build();

      userAccessor.saveItem(user);
      return ResponseEntity.ok(new BaseResponse<>("User registered successfully!"));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(e.getMessage()));
    }
  }

  @Override
  public ResponseEntity<?> authenticate(LoginRequest request) {
    try {
      User user = userAccessor.getByFilter(UserCommon.USERNAME, request.getUsername());
      if (user == null) {
        return ResponseEntity
          .status(HttpStatus.UNAUTHORIZED)
          .body(new BaseResponse<>("Error: User is not valid!"));
      } else if (!user.isEnabled()) {
        return ResponseEntity
          .status(HttpStatus.UNAUTHORIZED)
          .body(new BaseResponse<>("Error: Please verify your account by email!"));
      }

      if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        return ResponseEntity
          .status(HttpStatus.UNAUTHORIZED)
          .body(new BaseResponse<>("Error: Invalid credentials!"));
      }

      String token = jwtUtil.generateToken(user.getUsername(), user.getRoles());
      activityLogService.logActivity(user.getUsername(), "User Logged In");
      return ResponseEntity.ok(new BaseResponse<>(new AuthResponse(user.getId(), token)));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(e.getMessage()));
    }
  }

  @Override
  public ResponseEntity<?> changePassword(ChangePasswordRequest request) {
    if (request == null || request.getUserId() == null) {
      return ResponseEntity.badRequest().body(new BaseResponse<>("Error: Request is null!"));
    }
    User user = userAccessor.getItemById(request.getUserId());
    if (user == null) {
      return ResponseEntity.badRequest().body(new BaseResponse<>(String.format("Error: There's no user with userId: %s!", request.getUserId())));
    }

    if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
      return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new BaseResponse<>("Error: Wrong Password!"));
    }
    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    userAccessor.saveItem(user);
    return ResponseEntity.ok(new BaseResponse<>("Password updated successfully!"));
  }
}
