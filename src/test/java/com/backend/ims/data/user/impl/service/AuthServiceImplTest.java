package com.backend.ims.data.user.impl.service;

import com.backend.ims.data.user.api.common.UserCommon;
import com.backend.ims.data.user.api.model.User;
import com.backend.ims.data.user.api.model.request.LoginRequest;
import com.backend.ims.data.user.api.model.request.RegistrationRequest;
import com.backend.ims.data.user.api.model.response.AuthResponse;
import com.backend.ims.data.user.impl.accessor.UserAccessor;
import com.backend.ims.general.util.JwtUtil;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

@Test
public class AuthServiceImplTest {

  @Mock
  private UserAccessor userAccessor;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtUtil jwtUtil;

  private AuthServiceImpl authService;

  @BeforeMethod(alwaysRun = true)
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    authService = new AuthServiceImpl(userAccessor, passwordEncoder, jwtUtil);
  }

  @Test
  public void testRegister_UsernameExists() {
    RegistrationRequest request = new RegistrationRequest();
    request.setUsername("existingUser");
    request.setEmail("new@example.com");

    Mockito.when(userAccessor.isExist(Mockito.eq(UserCommon.USERNAME), Mockito.eq(request.getUsername())))
      .thenReturn(true);
    ResponseEntity<?> response = authService.register(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    Assert.assertEquals(response.getBody(), "Error: Username is already taken!");
    Mockito.verify(userAccessor, Mockito.never()).saveItem(Mockito.any());
  }

  @Test
  public void testRegister_EmailExists() {
    RegistrationRequest request = new RegistrationRequest();
    request.setUsername("newUser");
    request.setEmail("existing@example.com");

    Mockito.when(userAccessor.isExist(Mockito.eq(UserCommon.USERNAME), Mockito.eq(request.getUsername())))
      .thenReturn(false);
    Mockito.when(userAccessor.isExist(Mockito.eq(UserCommon.EMAIL), Mockito.eq(request.getEmail())))
      .thenReturn(true);
    ResponseEntity<?> response = authService.register(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    Assert.assertEquals(response.getBody(), "Error: Email is already in use!");
    Mockito.verify(userAccessor, Mockito.never()).saveItem(Mockito.any());
  }

  @Test
  public void testRegister_Success() {
    RegistrationRequest request = new RegistrationRequest();
    request.setUsername("newUser");
    request.setEmail("new@example.com");
    request.setPassword("password");

    Mockito.when(userAccessor.isExist(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
    Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encodedPassword");
    ResponseEntity<?> response = authService.register(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    Assert.assertEquals(response.getBody(), "User registered successfully!");
    Mockito.verify(userAccessor).saveItem(Mockito.argThat(user ->
      user.getUsername().equals("newUser") &&
        user.getPassword().equals("encodedPassword")
    ));
  }

  @Test
  public void testRegister_Exception() {
    Mockito.when(userAccessor.saveItem(Mockito.any())).thenThrow(RuntimeException.class);
    ResponseEntity<?> response = authService.register(new RegistrationRequest());
    Assert.assertEquals(response.getStatusCode(), HttpStatus.BAD_GATEWAY);
  }

  @Test
  public void testAuthenticate_UserNull() {
    LoginRequest request = new LoginRequest();
    request.setUsername("validUser");
    request.setPassword("wrongPassword");

    Mockito.when(userAccessor.getByFilter(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
    ResponseEntity<?> response = authService.authenticate(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.UNAUTHORIZED);
    Assert.assertEquals(response.getBody(), "Error: User is not valid!");
  }

  @Test
  public void testAuthenticate_UserDisabled() {
    User user = User.builder()
      .username("validUser")
      .password("encodedPassword")
      .enabled(false)
      .build();
    LoginRequest request = new LoginRequest();
    request.setUsername("validUser");
    request.setPassword("wrongPassword");

    Mockito.when(userAccessor.getByFilter(Mockito.anyString(), Mockito.anyString())).thenReturn(user);
    ResponseEntity<?> response = authService.authenticate(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.UNAUTHORIZED);
    Assert.assertEquals(response.getBody(), "Error: Please verify your account by email!");
  }

  @Test
  public void testAuthenticate_InvalidPassword() {
    User user = User.builder()
      .username("validUser")
      .password("encodedPassword")
      .enabled(true)
      .build();

    LoginRequest request = new LoginRequest();
    request.setUsername("validUser");
    request.setPassword("wrongPassword");

    Mockito.when(userAccessor.getByFilter(Mockito.anyString(), Mockito.anyString())).thenReturn(user);
    Mockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
    ResponseEntity<?> response = authService.authenticate(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.UNAUTHORIZED);
    Assert.assertEquals(response.getBody(), "Error: Invalid credentials!");
  }

  @Test
  public void testAuthenticate_Success() {
    User user = User.builder()
      .username("validUser")
      .password("encodedPassword")
      .enabled(true)
      .roles(List.of("ROLE_USER"))
      .build();
    LoginRequest request = new LoginRequest();
    request.setUsername("validUser");
    request.setPassword("encodedPassword");

    Mockito.when(userAccessor.getByFilter(Mockito.anyString(), Mockito.anyString())).thenReturn(user);
    Mockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
    Mockito.when(jwtUtil.generateToken(Mockito.anyString(), Mockito.anyList())).thenReturn("jwtToken");

    ResponseEntity<?> response = authService.authenticate(request);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    Assert.assertEquals(((AuthResponse) response.getBody()).getToken(), "jwtToken");
    Mockito.verify(jwtUtil).generateToken(Mockito.eq("validUser"), Mockito.eq(List.of("ROLE_USER")));
  }

  @Test
  public void testAuthenticate_Exception() {
    Mockito.when(userAccessor.getByFilter(Mockito.any(), Mockito.any())).thenThrow(RuntimeException.class);
    ResponseEntity<?> response = authService.authenticate(new LoginRequest());
    Assert.assertEquals(response.getStatusCode(), HttpStatus.BAD_GATEWAY);
  }
}
