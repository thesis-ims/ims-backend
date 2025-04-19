package com.backend.ims.data.user.api.service;

import com.backend.ims.general.model.request.PaginationRequest;
import com.backend.ims.data.user.api.model.request.UpdateRoleRequest;
import com.backend.ims.data.user.api.model.request.UserRequest;
import org.springframework.http.ResponseEntity;

public interface UserService {
  /**
   * This method is used to get all user data
   *
   * @param   request request will contains page and size per page to shown
   * @return  all user data
   */
  ResponseEntity<?> getAllUsers(PaginationRequest request);

  /**
   * This method is used to get user by user id
   *
   * @param   request user id
   * @return  detailed user information
   */
  ResponseEntity<?> getUserById(UserRequest request);

  /**
   * This method is used to update user data
   *
   * @param   request user data to update
   * @return  user data response
   */
  ResponseEntity<?> updateUser(UserRequest request);

  /**
   * This method will delete user data by user id
   *
   * @param   request user email to delete
   * @return  base response
   */
  ResponseEntity<?> deleteUser(UserRequest request);

  /**
   * This method is used to update user role
   *
   * @param   request request contains id and role
   * @return  user data response
   */
  ResponseEntity<?> updateUserRole(UpdateRoleRequest request);
}
