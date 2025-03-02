package com.backend.ims.data.user.impl.service;

import com.backend.ims.data.user.api.model.User;
import com.backend.ims.data.user.api.model.request.PaginationRequest;
import com.backend.ims.data.user.api.model.request.UpdateRoleRequest;
import com.backend.ims.data.user.api.model.request.UserRequest;
import com.backend.ims.data.user.api.model.response.PaginatedUserResponse;
import com.backend.ims.data.user.api.service.UserService;
import com.backend.ims.data.user.impl.accessor.UserAccessor;
import com.backend.ims.general.model.BaseResponse;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

@Test
public class UserServiceTest {

  @Mock
  private UserAccessor userAccessor;

  @Mock
  private MongoTemplate mongoTemplate;

  private UserService userService;

  @BeforeMethod(alwaysRun = true)
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    userService = new UserServiceImpl(userAccessor);
  }

  @Test
  public void testGetAllUsers_RequestNull() {
    ResponseEntity<?> response = userService.getAllUsers(null);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Error: Request is null!");
  }

  @Test
  public void testGetAllUsers_Success() {
    PaginationRequest request = new PaginationRequest();
    request.setPage(1);
    request.setSize(10);
    User user = User.builder()
      .roles(List.of("ROLE_USER"))
      .build();
    User userAdmin = User.builder()
      .roles(List.of("ROLE_ADMIN"))
      .build();
    Mockito.when(userAccessor.getAllItems()).thenReturn(List.of(user, userAdmin));
    ResponseEntity<?> response = userService.getAllUsers(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Successfully Getting All User Data");
    Assert.assertEquals(((BaseResponse<PaginatedUserResponse>) response.getBody()).getData().getUsers().size(), 2);
  }

  @Test
  public void testGetAllUsers_Exception() {
    Mockito.when(userAccessor.getAllItems()).thenThrow(RuntimeException.class);
    ResponseEntity<?> response = userService.getAllUsers(new PaginationRequest());
    Assert.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  public void testGetUserById_RequestNull() {
    ResponseEntity<?> response = userService.getUserById(null);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Error: Request is null!");
  }

  @Test
  public void testGetUserById_UserNull() {
    UserRequest request = new UserRequest();
    request.setUserId("123");
    Mockito.when(userAccessor.getItemById(Mockito.anyString())).thenReturn(null);
    ResponseEntity<?> response = userService.getUserById(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Error: There's no user with userId: 123!");
  }

  @Test
  public void testGetUserById_Success() {
    UserRequest request = new UserRequest();
    request.setUserId("123");
    Mockito.when(userAccessor.getItemById(Mockito.anyString())).thenReturn(new User());
    ResponseEntity<?> response = userService.getUserById(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Successfully Getting User Data");
    Assert.assertEquals((((BaseResponse) response.getBody()).getData().getClass()), User.class);
  }

  @Test
  public void testGetUserById_Exception() {
    UserRequest request = new UserRequest();
    request.setUserId("123");
    Mockito.when(userAccessor.getItemById(Mockito.anyString())).thenThrow(RuntimeException.class);
    ResponseEntity<?> response = userService.getUserById(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  public void testUpdateUser_RequestNull() {
    ResponseEntity<?> response = userService.updateUser(null);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Error: Request is null!");
  }

  @Test
  public void testUpdateUser_UserNull() {
    UserRequest request = new UserRequest();
    request.setUserId("123");
    Mockito.when(userAccessor.getItemById(Mockito.anyString())).thenReturn(null);
    ResponseEntity<?> response = userService.updateUser(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Error: There's no user with userId: 123!");
  }

  @Test
  public void testUpdateUser_Success() {
    UserRequest request = new UserRequest();
    request.setUserId("123");
    Mockito.when(userAccessor.getItemById(Mockito.anyString())).thenReturn(new User());
    ResponseEntity<?> response = userService.updateUser(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Successfully Update User Data");
  }

  @Test
  public void testUpdateUser_Exception() {
    UserRequest request = new UserRequest();
    request.setUserId("123");
    Mockito.when(userAccessor.getItemById(Mockito.anyString())).thenThrow(RuntimeException.class);
    ResponseEntity<?> response = userService.updateUser(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  public void testDeleteUser_RequestNull() {
    ResponseEntity<?> response = userService.deleteUser(null);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Error: Request is null!");
  }

  @Test
  public void testDeleteUser_Success() {
    UserRequest request = new UserRequest();
    request.setUserId("123");
    ResponseEntity<?> response = userService.deleteUser(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Successfully Delete User Data");
  }


  @Test
  public void testUpdateUserRole_RequestNull() {
    ResponseEntity<?> response = userService.updateUserRole(null);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Error: Request is null!");
  }

  @Test
  public void testUpdateUserRole_UserNull() {
    UpdateRoleRequest request = new UpdateRoleRequest();
    request.setUserId("123");
    request.setRole("ROLE_ADMIN");
    Mockito.when(userAccessor.getItemById(Mockito.anyString())).thenReturn(null);
    ResponseEntity<?> response = userService.updateUserRole(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Error: There's no user with userId: 123!");
  }

  @Test
  public void testUpdateUserRole_Success() {
    UpdateRoleRequest request = new UpdateRoleRequest();
    request.setUserId("123");
    request.setRole("ROLE_ADMIN");
    Mockito.when(userAccessor.getItemById(Mockito.anyString())).thenReturn(new User());
    ResponseEntity<?> response = userService.updateUserRole(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Successfully Update User 123 Role's to ROLE_ADMIN");
  }

  @Test
  public void testUpdateUserRole_Exception() {
    UpdateRoleRequest request = new UpdateRoleRequest();
    request.setUserId("123");
    request.setRole("ROLE_ADMIN");
    Mockito.when(userAccessor.getItemById(Mockito.anyString())).thenThrow(RuntimeException.class);
    ResponseEntity<?> response = userService.updateUserRole(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}

