package com.backend.ims.data.user.api.service;

import com.backend.ims.data.user.api.model.request.LoginRequest;
import com.backend.ims.data.user.api.model.request.RegistrationRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
  /**
   * This method used to store register user to db
   * @param request request
   * @return                    registration result
   */
  ResponseEntity<?> register(RegistrationRequest request);

  /**
   * This method used to validate user login
   * @param request  request
   * @return              authentication result
   */
  ResponseEntity<?> authenticate(LoginRequest request);
}
