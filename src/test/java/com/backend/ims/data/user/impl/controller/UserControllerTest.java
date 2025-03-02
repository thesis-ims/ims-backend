package com.backend.ims.data.user.impl.controller;

import com.backend.ims.data.user.api.model.request.PaginationRequest;
import com.backend.ims.data.user.api.model.request.UpdateRoleRequest;
import com.backend.ims.data.user.api.model.request.UserRequest;
import com.backend.ims.data.user.api.service.UserService;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class UserControllerTest {

  @Mock
  private UserService userService;

  private UserController userController;

  @BeforeMethod(alwaysRun = true)
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    userController = new UserController(userService);
  }

  @Test
  public void testGetAllUsers() {
    Mockito.when(userService.getAllUsers(Mockito.any())).thenReturn(new ResponseEntity<>(HttpStatusCode.valueOf(200)));
    ResponseEntity<?> actualResponse = userController.getAllUsers(new PaginationRequest());
    Assert.assertEquals(actualResponse.getStatusCode(), HttpStatus.OK);
  }

  @Test
  public void testGetUserById() {
    Mockito.when(userService.getUserById(Mockito.any())).thenReturn(new ResponseEntity<>(HttpStatusCode.valueOf(200)));
    ResponseEntity<?> actualResponse = userController.getUserById(new UserRequest());
    Assert.assertEquals(actualResponse.getStatusCode(), HttpStatus.OK);
  }

  @Test
  public void testUpdateUser() {
    Mockito.when(userService.updateUser(Mockito.any())).thenReturn(new ResponseEntity<>(HttpStatusCode.valueOf(200)));
    ResponseEntity<?> actualResponse = userController.updateUser(new UserRequest());
    Assert.assertEquals(actualResponse.getStatusCode(), HttpStatus.OK);
  }

  @Test
  public void testDeleteUser() {
    Mockito.when(userService.deleteUser(Mockito.any())).thenReturn(new ResponseEntity<>(HttpStatusCode.valueOf(200)));
    ResponseEntity<?> actualResponse = userController.deleteUser(new UserRequest());
    Assert.assertEquals(actualResponse.getStatusCode(), HttpStatus.OK);
  }

  @Test
  public void testUpdateUserRole() {
    Mockito.when(userService.updateUserRole(Mockito.any())).thenReturn(new ResponseEntity<>(HttpStatusCode.valueOf(200)));
    ResponseEntity<?> actualResponse = userController.updateUserRole(new UpdateRoleRequest());
    Assert.assertEquals(actualResponse.getStatusCode(), HttpStatus.OK);
  }
}
