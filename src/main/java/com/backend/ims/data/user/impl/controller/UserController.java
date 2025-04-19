package com.backend.ims.data.user.impl.controller;

import com.backend.ims.general.model.request.PaginationRequest;
import com.backend.ims.data.user.api.model.request.UpdateRoleRequest;
import com.backend.ims.data.user.api.model.request.UserRequest;
import com.backend.ims.data.user.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/get-all-user")
  public ResponseEntity<?> getAllUsers(@RequestBody PaginationRequest request) {
    return userService.getAllUsers(request);
  }

  @PostMapping("/get-user")
  public ResponseEntity<?> getUserById(@RequestBody UserRequest request) {
    return userService.getUserById(request);
  }

  @PostMapping("/update")
  public ResponseEntity<?> updateUser(@RequestBody UserRequest request) {
    return userService.updateUser(request);
  }

  @PostMapping("/delete")
  public ResponseEntity<?> deleteUser(@RequestBody UserRequest request) {
    return userService.deleteUser(request);
  }

  @PostMapping("/update-role")
  public ResponseEntity<?> updateUserRole(@RequestBody UpdateRoleRequest request) {
    return userService.updateUserRole(request);
  }
}
