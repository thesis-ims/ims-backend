package com.backend.ims.data.user.impl.controller;

import com.backend.ims.data.user.api.model.request.LoginRequest;
import com.backend.ims.data.user.api.model.request.RegistrationRequest;
import com.backend.ims.data.user.api.service.AuthService;
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
public class AuthControllerTest {

  @Mock
  private AuthService authService;

  private AuthController authController;

  @BeforeMethod(alwaysRun = true)
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    authController = new AuthController(authService);
  }

  @Test
  public void testLogin() {
    LoginRequest request = new LoginRequest();
    request.setUsername("testuser");
    request.setPassword("password");

    Mockito.when(authService.authenticate(Mockito.any())).thenReturn(new ResponseEntity<>(HttpStatusCode.valueOf(200)));
    ResponseEntity<?> actualResponse = authController.login(request);
    Assert.assertEquals(actualResponse.getStatusCode(), HttpStatus.OK);
  }

  @Test
  public void testRegister() {
    RegistrationRequest request = new RegistrationRequest();
    request.setEmail("test@example.com");
    request.setUsername("testuser");
    request.setPassword("password");

    Mockito.when(authService.register(Mockito.any())).thenReturn(new ResponseEntity<>(HttpStatusCode.valueOf(200)));
    ResponseEntity<?> actualResponse = authController.register(request);
    Assert.assertEquals(actualResponse.getStatusCode(), HttpStatus.OK);
  }
}
