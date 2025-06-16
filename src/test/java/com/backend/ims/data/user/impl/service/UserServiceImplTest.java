package com.backend.ims.data.user.impl.service;

import com.backend.ims.data.activitylog.api.model.ActivityLog;
import com.backend.ims.data.activitylog.impl.accessor.ActivityLogAccessor;
import com.backend.ims.data.product.api.model.Product;
import com.backend.ims.data.product.impl.accessor.ProductAccessor;
import com.backend.ims.data.user.api.model.User;
import com.backend.ims.data.user.api.model.request.UpdateRoleRequest;
import com.backend.ims.data.user.api.model.request.UserRequest;
import com.backend.ims.data.user.api.service.UserService;
import com.backend.ims.data.user.impl.accessor.UserAccessor;
import com.backend.ims.general.model.BaseResponse;
import com.backend.ims.general.model.request.PaginationRequest;
import com.backend.ims.general.model.response.PaginatedResponse;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

@Test
public class UserServiceImplTest {

  @Mock
  private UserAccessor userAccessor;

  @Mock
  private ProductAccessor productAccessor;
  @Mock
  private ActivityLogAccessor activityLogAccessor;

  @Mock
  private MongoTemplate mongoTemplate;

  private UserService userService;

  @BeforeMethod(alwaysRun = true)
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    userService = new UserServiceImpl(userAccessor, productAccessor, activityLogAccessor);
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
    Assert.assertEquals(((BaseResponse<PaginatedResponse>) response.getBody()).getData().getObject().size(), 2);
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
  public void testUpdateUser_UserExist() {
    UserRequest request = new UserRequest();
    request.setUserId("123");
    request.setUsername("newUser");
    User existingUser = new User();
    existingUser.setUsername("existingUser");
    Mockito.when(userAccessor.getItemById(Mockito.anyString())).thenReturn(existingUser);
    Mockito.when(userAccessor.isExist(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
    ResponseEntity<?> response = userService.updateUser(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Error: Username newUser is already taken!");
  }

  @Test
  public void testUpdateUser_EmailExist() {
    UserRequest request = new UserRequest();
    request.setUserId("123");
    request.setUsername("existingUser");
    request.setEmail("test@email.com");
    User existingUser = new User();
    existingUser.setUsername("existingUser");
    existingUser.setEmail("test2@email.com");
    Mockito.when(userAccessor.getItemById(Mockito.anyString())).thenReturn(existingUser);
    Mockito.when(userAccessor.isExist(Mockito.anyString(), Mockito.anyString()))
      .thenReturn(false)
      .thenReturn(true);
    ResponseEntity<?> response = userService.updateUser(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Error: Email test@email.com is already in use!");
  }

  @Test
  public void testUpdateUser_Success() {
    // Arrange
    UserRequest request = new UserRequest();
    request.setUserId("123");
    request.setUsername("updatedUsername");
    request.setEmail("updatedEmail@example.com");

    User existingUser = new User();
    existingUser.setUsername("existingUsername");
    existingUser.setEmail("existingEmail@example.com");
    existingUser.setRoles(List.of("ROLE_USER"));

    Product product = new Product();
    product.setCreatedBy("existingUsername");

    ActivityLog activityLog = new ActivityLog();
    activityLog.setUsername("existingUsername");

    Mockito.when(userAccessor.getItemById(Mockito.anyString())).thenReturn(existingUser);
    Mockito.when(userAccessor.isExist(Mockito.anyString(), Mockito.anyString()))
      .thenReturn(false)
      .thenReturn(false);
    Mockito.when(productAccessor.getAllItems()).thenReturn(List.of(product));
    Mockito.when(activityLogAccessor.getAllItems()).thenReturn(List.of(activityLog));
    ResponseEntity<?> response = userService.updateUser(request);
    // Assert
    Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    Assert.assertEquals(((BaseResponse) response.getBody()).getMessage(), "Successfully Update User Data");

    // Verify that the SecurityContextHolder is updated with the new username
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Assert.assertNotNull(authentication);
    Assert.assertEquals(authentication.getName(), "updatedUsername");
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

