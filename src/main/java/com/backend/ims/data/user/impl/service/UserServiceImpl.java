package com.backend.ims.data.user.impl.service;

import com.backend.ims.data.user.api.common.UserCommon;
import com.backend.ims.data.user.api.model.User;
import com.backend.ims.data.user.api.model.request.UpdateRoleRequest;
import com.backend.ims.data.user.api.model.request.UserRequest;
import com.backend.ims.data.user.api.service.UserService;
import com.backend.ims.data.user.impl.accessor.UserAccessor;
import com.backend.ims.data.user.impl.util.UserUtil;
import com.backend.ims.general.model.BaseResponse;
import com.backend.ims.general.model.request.PaginationRequest;
import com.backend.ims.general.model.response.PaginatedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

  private final UserAccessor userAccessor;

  @Autowired
  public UserServiceImpl(UserAccessor userAccessor) {
    this.userAccessor = userAccessor;
  }

  @Override
  public ResponseEntity<?> getAllUsers(PaginationRequest request) {
    try {
      if (request == null) {
        return ResponseEntity.badRequest().body(new BaseResponse<>("Error: Request is null!"));
      }

      List<User> allUsers = userAccessor.getAllItems();
      List<User> sortedUsers = allUsers.stream()
        .sorted(UserUtil::compare)
        .toList();

      int size = request.getSize();
      int page = request.getPage();
      int start = Math.max(0, (page - 1) * size);
      int end = Math.min(start + size, allUsers.size());

      List<User> paginatedUsers = sortedUsers.subList(start, end);
      List<User> sanitizedUsers = paginatedUsers.stream()
        .peek(UserUtil::hideSensitiveData)
        .toList();

      PaginatedResponse<User> response = PaginatedResponse.<User>builder()
        .object(sanitizedUsers)
        .total(allUsers.size())
        .page(page)
        .size(size)
        .totalPages((int) Math.ceil((double) allUsers.size() / size))
        .build();

      return ResponseEntity.ok(new BaseResponse<>("Successfully Getting All User Data", response));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(e.getMessage()));
    }
  }

  @Override
  public ResponseEntity<?> getUserById(UserRequest request) {
    try {
      if (request == null || request.getUserId() == null) {
        return ResponseEntity.badRequest().body(new BaseResponse<>("Error: Request is null!"));
      }
      User user = userAccessor.getItemById(request.getUserId());
      if (user == null) {
        return ResponseEntity.badRequest().body(new BaseResponse<>(String.format("Error: There's no user with userId: %s!", request.getUserId())));
      }
      UserUtil.hideSensitiveData(user);
      return ResponseEntity.ok(new BaseResponse<>("Successfully Getting User Data", user));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(e.getMessage()));
    }
  }

  @Override
  public ResponseEntity<?> updateUser(UserRequest request) {
    try {
      if (request == null || request.getUserId() == null) {
        return ResponseEntity.badRequest().body(new BaseResponse<>("Error: Request is null!"));
      }
      User user = userAccessor.getItemById(request.getUserId());
      if (user == null) {
        return ResponseEntity.badRequest().body(new BaseResponse<>(String.format("Error: There's no user with userId: %s!", request.getUserId())));
      }

      if (userAccessor.isExist(UserCommon.USERNAME, request.getUsername()) &&
        !user.getUsername().equals(request.getUsername())) {
        return ResponseEntity
          .badRequest()
          .body(new BaseResponse<>(String.format("Error: Username %s is already taken!", request.getUsername())));
      }

      if (userAccessor.isExist(UserCommon.EMAIL, request.getEmail()) &&
        !user.getEmail().equals(request.getEmail())) {
        return ResponseEntity
          .badRequest()
          .body(new BaseResponse<>(String.format("Error: Email %s is already in use!", request.getEmail())));
      }

      user.setEmail(request.getEmail());
      user.setUsername(request.getUsername());
      user.setGender(request.getGender());
      user.setPhoneNumber(request.getPhoneNumber());
      user.setDob(request.getDob());
      user.setImage(request.getImage());
      userAccessor.saveItem(user);
      return ResponseEntity.ok(new BaseResponse<>("Successfully Update User Data"));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(e.getMessage()));
    }
  }

  @Override
  public ResponseEntity<?> deleteUser(UserRequest request) {
    try {
      if (request == null || request.getUserId() == null) {
        return ResponseEntity.badRequest().body(new BaseResponse<>("Error: Request is null!"));
      }
      userAccessor.deleteItem(request.getUserId());
      return ResponseEntity.ok(new BaseResponse<>("Successfully Delete User Data"));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(e.getMessage()));
    }
  }

  @Override
  public ResponseEntity<?> updateUserRole(UpdateRoleRequest request) {
    try {
      if (request == null || request.getUserId() == null) {
        return ResponseEntity.badRequest().body(new BaseResponse<>("Error: Request is null!"));
      }
      User user = userAccessor.getItemById(request.getUserId());
      if (user == null) {
        return ResponseEntity.badRequest().body(new BaseResponse<>(String.format("Error: There's no user with userId: %s!", request.getUserId())));
      }
      user.setRoles(List.of(request.getRole()));
      userAccessor.saveItem(user);
      return ResponseEntity.ok(new BaseResponse<>(
        String.format("Successfully Update User %s Role's to %s", request.getUserId(), request.getRole())));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(e.getMessage()));
    }
  }
}
