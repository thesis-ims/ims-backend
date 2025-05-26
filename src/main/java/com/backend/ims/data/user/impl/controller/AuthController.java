package com.backend.ims.data.user.impl.controller;

import com.backend.ims.data.user.api.model.request.ChangePasswordRequest;
import com.backend.ims.data.user.api.model.request.LoginRequest;
import com.backend.ims.data.user.api.model.request.RegistrationRequest;
import com.backend.ims.data.user.api.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  @Autowired
  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegistrationRequest request) {
    return authService.register(request);
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    return authService.authenticate(request);
  }

  @PostMapping("/change-password")
  public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
    return authService.changePassword(request);
  }
}
